import { GameScenario } from './game-notation';
import * as fs from 'fs';
import * as path from 'path';
import * as yaml from 'js-yaml';

/**
 * 棋谱数据加载器
 * 支持JSON、YAML等多种格式
 */
export class ScenarioLoader {
  private static scenariosCache: Map<string, GameScenario> = new Map();
  private static scenariosDirectory = path.join(__dirname, 'scenarios');

  /**
   * 从文件加载单个场景
   */
  static async loadScenario(filename: string): Promise<GameScenario> {
    const cacheKey = filename;
    
    // 检查缓存
    if (this.scenariosCache.has(cacheKey)) {
      return this.scenariosCache.get(cacheKey)!;
    }

    const filePath = path.join(this.scenariosDirectory, filename);
    
    if (!fs.existsSync(filePath)) {
      throw new Error(`Scenario file not found: ${filename}`);
    }

    const fileContent = fs.readFileSync(filePath, 'utf8');
    const fileExtension = path.extname(filename).toLowerCase();
    
    let scenario: GameScenario;
    
    switch (fileExtension) {
      case '.json':
        scenario = JSON.parse(fileContent);
        break;
        
      case '.yaml':
      case '.yml':
        scenario = yaml.load(fileContent) as GameScenario;
        break;
        
      default:
        throw new Error(`Unsupported file format: ${fileExtension}`);
    }
    
    // 验证场景数据
    this.validateScenario(scenario, filename);
    
    // 缓存结果
    this.scenariosCache.set(cacheKey, scenario);
    
    return scenario;
  }

  /**
   * 加载目录中的所有场景
   */
  static async loadAllScenarios(): Promise<Record<string, GameScenario>> {
    const scenarios: Record<string, GameScenario> = {};
    
    if (!fs.existsSync(this.scenariosDirectory)) {
      console.warn(`Scenarios directory not found: ${this.scenariosDirectory}`);
      return scenarios;
    }
    
    const files = fs.readdirSync(this.scenariosDirectory);
    
    for (const file of files) {
      if (this.isSupportedFile(file)) {
        try {
          const scenario = await this.loadScenario(file);
          scenarios[scenario.id] = scenario;
        } catch (error) {
          console.error(`Failed to load scenario ${file}:`, error);
        }
      }
    }
    
    return scenarios;
  }

  /**
   * 按标签筛选场景
   */
  static async loadScenariosByTags(tags: string[]): Promise<GameScenario[]> {
    const allScenarios = await this.loadAllScenarios();
    
    return Object.values(allScenarios).filter(scenario => {
      const scenarioTags = (scenario as any).tags || [];
      return tags.some(tag => scenarioTags.includes(tag));
    });
  }

  /**
   * 按难度加载场景
   */
  static async loadScenariosByDifficulty(difficulty: string): Promise<GameScenario[]> {
    const allScenarios = await this.loadAllScenarios();
    
    return Object.values(allScenarios).filter(scenario => {
      const metadata = (scenario as any).metadata || {};
      return metadata.difficulty === difficulty;
    });
  }

  /**
   * 保存场景到文件
   */
  static async saveScenario(scenario: GameScenario, filename: string, format: 'json' | 'yaml' = 'json'): Promise<void> {
    const filePath = path.join(this.scenariosDirectory, filename);
    
    // 确保目录存在
    if (!fs.existsSync(this.scenariosDirectory)) {
      fs.mkdirSync(this.scenariosDirectory, { recursive: true });
    }
    
    let content: string;
    
    if (format === 'yaml') {
      content = yaml.dump(scenario, {
        indent: 2,
        lineWidth: 80,
        noRefs: true
      });
    } else {
      content = JSON.stringify(scenario, null, 2);
    }
    
    fs.writeFileSync(filePath, content, 'utf8');
    
    // 更新缓存
    this.scenariosCache.set(filename, scenario);
  }

  /**
   * 验证场景数据完整性
   */
  private static validateScenario(scenario: GameScenario, filename: string): void {
    const errors: string[] = [];
    
    if (!scenario.id) {
      errors.push('Missing scenario id');
    }
    
    if (!scenario.name) {
      errors.push('Missing scenario name');
    }
    
    if (!scenario.setup) {
      errors.push('Missing scenario setup');
    }
    
    if (!scenario.moves || !Array.isArray(scenario.moves)) {
      errors.push('Missing or invalid moves array');
    }
    
    // 验证移动数据
    scenario.moves?.forEach((move, index) => {
      if (!move.type) {
        errors.push(`Move ${index}: missing type`);
      }
      
      if (!move.player) {
        errors.push(`Move ${index}: missing player`);
      }
      
      if (!move.to) {
        errors.push(`Move ${index}: missing 'to' position`);
      }
      
      if (move.type === 'move' && !move.from) {
        errors.push(`Move ${index}: move action requires 'from' position`);
      }
    });
    
    if (errors.length > 0) {
      throw new Error(`Invalid scenario in ${filename}:\n${errors.join('\n')}`);
    }
  }

  /**
   * 检查文件格式是否支持
   */
  private static isSupportedFile(filename: string): boolean {
    const supportedExtensions = ['.json', '.yaml', '.yml'];
    const extension = path.extname(filename).toLowerCase();
    return supportedExtensions.includes(extension);
  }

  /**
   * 清空缓存
   */
  static clearCache(): void {
    this.scenariosCache.clear();
  }

  /**
   * 获取场景统计信息
   */
  static async getScenarioStats(): Promise<{
    total: number;
    byDifficulty: Record<string, number>;
    byGodCard: Record<string, number>;
    byTags: Record<string, number>;
  }> {
    const allScenarios = await this.loadAllScenarios();
    const scenarios = Object.values(allScenarios);
    
    const stats = {
      total: scenarios.length,
      byDifficulty: {} as Record<string, number>,
      byGodCard: {} as Record<string, number>,
      byTags: {} as Record<string, number>
    };
    
    scenarios.forEach(scenario => {
      // 统计难度分布
      const difficulty = (scenario as any).metadata?.difficulty || 'unknown';
      stats.byDifficulty[difficulty] = (stats.byDifficulty[difficulty] || 0) + 1;
      
      // 统计神卡使用
      const godCards = [
        scenario.setup.players.player1.godCard,
        scenario.setup.players.player2.godCard
      ];
      
      godCards.forEach(god => {
        stats.byGodCard[god] = (stats.byGodCard[god] || 0) + 1;
      });
      
      // 统计标签
      const tags = (scenario as any).tags || [];
      tags.forEach((tag: string) => {
        stats.byTags[tag] = (stats.byTags[tag] || 0) + 1;
      });
    });
    
    return stats;
  }
}

/**
 * 场景管理器 - 提供高级的场景管理功能
 */
export class ScenarioManager {
  /**
   * 创建场景集合
   */
  static createScenarioSuite(name: string, scenarioIds: string[]): ScenarioSuite {
    return {
      name,
      scenarios: scenarioIds,
      metadata: {
        created: new Date().toISOString(),
        version: '1.0'
      }
    };
  }

  /**
   * 验证场景依赖关系
   */
  static async validateDependencies(scenario: GameScenario): Promise<string[]> {
    const warnings: string[] = [];
    
    // 检查引用的相关场景是否存在
    const metadata = (scenario as any).metadata || {};
    const relatedScenarios = metadata.relatedScenarios || [];
    
    for (const relatedId of relatedScenarios) {
      try {
        await ScenarioLoader.loadScenario(`${relatedId}.json`);
      } catch {
        try {
          await ScenarioLoader.loadScenario(`${relatedId}.yaml`);
        } catch {
          warnings.push(`Related scenario not found: ${relatedId}`);
        }
      }
    }
    
    return warnings;
  }

  /**
   * 生成场景变体
   */
  static generateScenarioVariant(
    baseScenario: GameScenario, 
    modifications: ScenarioModification
  ): GameScenario {
    const variant = JSON.parse(JSON.stringify(baseScenario)); // 深度克隆
    
    // 应用修改
    if (modifications.setup) {
      Object.assign(variant.setup, modifications.setup);
    }
    
    if (modifications.moves) {
      if (modifications.moves.add) {
        variant.moves.push(...modifications.moves.add);
      }
      
      if (modifications.moves.remove) {
        modifications.moves.remove.forEach(step => {
          variant.moves = variant.moves.filter((move: any) => move.step !== step);
        });
      }
      
      if (modifications.moves.replace) {
        modifications.moves.replace.forEach(({ step, newMove }) => {
          const index = variant.moves.findIndex((move: any) => move.step === step);
          if (index !== -1) {
            variant.moves[index] = newMove;
          }
        });
      }
    }
    
    // 更新ID和名称
    variant.id = modifications.id || `${baseScenario.id}_variant`;
    variant.name = modifications.name || `${baseScenario.name} - Variant`;
    
    return variant;
  }
}

// 类型定义
interface ScenarioSuite {
  name: string;
  scenarios: string[];
  metadata: {
    created: string;
    version: string;
  };
}

interface ScenarioModification {
  id?: string;
  name?: string;
  setup?: Partial<GameScenario['setup']>;
  moves?: {
    add?: any[];
    remove?: number[];
    replace?: Array<{ step: number; newMove: any }>;
  };
}

export { ScenarioSuite, ScenarioModification };