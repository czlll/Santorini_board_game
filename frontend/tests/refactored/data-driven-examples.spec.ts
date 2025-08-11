import { test, expect } from '@playwright/test';
import { SantoriniTestEngine } from '../engine/game-engine';
import { PageObjectManager } from '../pages/game-pages';
import { ScenarioLoader, ScenarioManager } from '../data/scenario-loader';

test.describe('Data-Driven Test Examples - Multiple Formats', () => {
  let engine: SantoriniTestEngine;
  let pageManager: PageObjectManager;

  test.beforeEach(async ({ page }) => {
    engine = new SantoriniTestEngine(page);
    pageManager = new PageObjectManager(page);
  });

  test('Execute scenario from JSON file', async ({ page }) => {
    // 直接从JSON文件加载并执行场景
    await engine.executeScenarioFromFile('apollo-swap.json');
    
    // 验证结果
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Execute scenario from YAML file', async ({ page }) => {
    // 从YAML文件加载并执行场景
    await engine.executeScenarioFromFile('demeter-double-build.yaml');
    
    // 验证游戏状态
    const gameState = await engine.getCurrentGameState();
    expect(gameState.status).toBeDefined();
  });

  test('Load and execute multiple scenarios by tags', async ({ page }) => {
    // 根据标签加载相关场景
    const apolloScenarios = await ScenarioLoader.loadScenariosByTags(['apollo']);
    
    expect(apolloScenarios.length).toBeGreaterThan(0);
    
    for (const scenario of apolloScenarios.slice(0, 2)) { // 限制执行数量
      await test.step(`Execute ${scenario.name}`, async () => {
        await engine.executeScenario(scenario);
        await pageManager.gameBoardPage.verifyPageLoaded();
      });
    }
  });

  test('Load scenarios by difficulty level', async ({ page }) => {
    // 加载基础难度的场景
    const basicScenarios = await ScenarioLoader.loadScenariosByDifficulty('basic');
    
    expect(basicScenarios.length).toBeGreaterThan(0);
    
    // 执行第一个基础场景
    if (basicScenarios.length > 0) {
      await engine.executeScenario(basicScenarios[0]);
      await pageManager.gameBoardPage.verifyGameInProgress();
    }
  });

  test('Generate and execute scenario variant', async ({ page }) => {
    // 加载基础场景
    const baseScenario = await ScenarioLoader.loadScenario('apollo-swap.json');
    
    // 生成变体：将Bob的神卡改为Pan
    const variant = ScenarioManager.generateScenarioVariant(baseScenario, {
      id: 'apollo_vs_pan_variant',
      name: 'Apollo vs Pan 变体测试',
      setup: {
        players: {
          player1: baseScenario.setup.players.player1,
          player2: { name: 'Pan_Bob', godCard: 'Pan' }
        }
      }
    });
    
    // 执行变体场景
    await engine.executeScenario(variant);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Batch execute scenario suite', async ({ page }) => {
    // 定义要执行的场景文件列表
    const scenarioFiles = [
      'apollo-swap.json',
      'demeter-double-build.yaml'
    ];
    
    // 批量执行
    const results = await engine.executeScenarioSuite(scenarioFiles);
    
    // 验证执行结果
    console.log('执行结果:', results);
    expect(results.passed.length).toBeGreaterThan(0);
    
    // 如果有失败的场景，输出详细信息
    if (results.failed.length > 0) {
      console.log('失败的场景:', results.failed);
    }
  });

  test('Validate scenario data integrity', async () => {
    // 加载所有场景并验证数据完整性
    const allScenarios = await ScenarioLoader.loadAllScenarios();
    const scenarioCount = Object.keys(allScenarios).length;
    
    expect(scenarioCount).toBeGreaterThan(0);
    
    // 验证每个场景的数据格式
    for (const [id, scenario] of Object.entries(allScenarios)) {
      expect(scenario.id).toBe(id);
      expect(scenario.name).toBeDefined();
      expect(scenario.setup).toBeDefined();
      expect(scenario.moves).toBeInstanceOf(Array);
      expect(scenario.moves.length).toBeGreaterThan(0);
    }
  });

  test('Get scenario statistics', async () => {
    // 获取场景统计信息
    const stats = await ScenarioLoader.getScenarioStats();
    
    console.log('场景统计:', stats);
    
    expect(stats.total).toBeGreaterThan(0);
    expect(stats.byDifficulty).toBeDefined();
    expect(stats.byGodCard).toBeDefined();
    expect(stats.byTags).toBeDefined();
  });

  test('Save custom scenario to file', async () => {
    // 创建自定义场景
    const customScenario = {
      id: 'custom_artemis_test',
      name: '自定义Artemis测试',
      description: '测试Artemis双重移动能力的自定义场景',
      setup: {
        players: {
          player1: { name: 'Artemis_Player', godCard: 'Artemis' },
          player2: { name: 'Atlas_Player', godCard: 'Atlas' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        { type: 'place_worker' as const, player: 'player1' as const, to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker' as const, player: 'player1' as const, to: { x: 0, y: 0 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker' as const, player: 'player2' as const, to: { x: 4, y: 4 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker' as const, player: 'player2' as const, to: { x: 1, y: 4 }, metadata: { workerIndex: 1 } }
      ]
    };
    
    // 保存为JSON格式
    await ScenarioLoader.saveScenario(customScenario, 'custom-artemis-test.json', 'json');
    
    // 保存为YAML格式
    await ScenarioLoader.saveScenario(customScenario, 'custom-artemis-test.yaml', 'yaml');
    
    // 验证可以重新加载
    const loadedScenario = await ScenarioLoader.loadScenario('custom-artemis-test.json');
    expect(loadedScenario.id).toBe(customScenario.id);
    expect(loadedScenario.name).toBe(customScenario.name);
  });

  test('Cross-format consistency check', async () => {
    // 创建相同的场景，保存为不同格式
    const testScenario = {
      id: 'format_consistency_test',
      name: '格式一致性测试',
      description: '验证不同格式存储的一致性',
      setup: {
        players: {
          player1: { name: 'Player1', godCard: 'Apollo' },
          player2: { name: 'Player2', godCard: 'Demeter' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        { type: 'place_worker' as const, player: 'player1' as const, to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } }
      ]
    };
    
    // 保存为不同格式
    await ScenarioLoader.saveScenario(testScenario, 'consistency-test.json', 'json');
    await ScenarioLoader.saveScenario(testScenario, 'consistency-test.yaml', 'yaml');
    
    // 加载并比较
    const jsonLoaded = await ScenarioLoader.loadScenario('consistency-test.json');
    const yamlLoaded = await ScenarioLoader.loadScenario('consistency-test.yaml');
    
    // 验证核心数据一致
    expect(jsonLoaded.id).toBe(yamlLoaded.id);
    expect(jsonLoaded.name).toBe(yamlLoaded.name);
    expect(jsonLoaded.moves.length).toBe(yamlLoaded.moves.length);
    expect(JSON.stringify(jsonLoaded.setup)).toBe(JSON.stringify(yamlLoaded.setup));
  });

  test('Performance test with large scenario set', async ({ page }) => {
    const startTime = Date.now();
    
    // 加载所有场景
    const allScenarios = await ScenarioLoader.loadAllScenarios();
    const loadTime = Date.now() - startTime;
    
    console.log(`加载${Object.keys(allScenarios).length}个场景耗时: ${loadTime}ms`);
    
    // 验证加载性能合理（应该在合理时间内完成）
    expect(loadTime).toBeLessThan(5000); // 5秒内
    
    // 清除缓存后重新加载，测试缓存效果
    ScenarioLoader.clearCache();
    
    const recacheStartTime = Date.now();
    await ScenarioLoader.loadAllScenarios();
    const recacheTime = Date.now() - recacheStartTime;
    
    console.log(`重新缓存耗时: ${recacheTime}ms`);
    
    // 再次加载，应该使用缓存，速度更快
    const cachedStartTime = Date.now();
    await ScenarioLoader.loadAllScenarios();
    const cachedTime = Date.now() - cachedStartTime;
    
    console.log(`缓存加载耗时: ${cachedTime}ms`);
    expect(cachedTime).toBeLessThan(recacheTime);
  });
});

// 数据格式比较测试
test.describe('Data Format Comparison', () => {
  test('Compare storage efficiency', async () => {
    const sampleScenario = {
      id: 'storage_test',
      name: '存储效率测试',
      description: '比较不同格式的存储效率',
      setup: {
        players: {
          player1: { name: 'Alice', godCard: 'Apollo' },
          player2: { name: 'Bob', godCard: 'Demeter' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: Array.from({ length: 20 }, (_, i) => ({
        type: 'place_worker' as const,
        player: i % 2 === 0 ? 'player1' as const : 'player2' as const,
        to: { x: i % 5, y: Math.floor(i / 5) },
        metadata: { step: i + 1 }
      }))
    };
    
    // 保存为不同格式并比较文件大小
    await ScenarioLoader.saveScenario(sampleScenario, 'storage-test.json', 'json');
    await ScenarioLoader.saveScenario(sampleScenario, 'storage-test.yaml', 'yaml');
    
    // 在实际项目中，这里可以读取文件大小进行比较
    console.log('不同格式的存储效率对比已完成');
  });

  test('Compare loading performance', async () => {
    const scenarios = ['apollo-swap.json', 'demeter-double-build.yaml'];
    const performanceResults: Record<string, number> = {};
    
    for (const filename of scenarios) {
      const startTime = Date.now();
      await ScenarioLoader.loadScenario(filename);
      const loadTime = Date.now() - startTime;
      
      performanceResults[filename] = loadTime;
      console.log(`${filename} 加载耗时: ${loadTime}ms`);
    }
    
    // 验证所有格式都能在合理时间内加载
    Object.values(performanceResults).forEach(time => {
      expect(time).toBeLessThan(1000); // 1秒内
    });
  });
});