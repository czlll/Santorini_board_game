import { GameScenario, GameMove, Position } from '../data/game-notation';

/**
 * 测试辅助工具集
 */
export class TestHelpers {
  /**
   * 创建基础工人放置场景
   */
  static createBasicWorkerPlacementScenario(
    player1Name: string = 'TestPlayer1',
    player2Name: string = 'TestPlayer2',
    player1God: string = 'Demeter',
    player2God: string = 'Pan'
  ): GameScenario {
    return {
      id: 'basic_placement',
      name: '基础工人放置',
      description: '测试基础工人放置流程',
      setup: {
        players: {
          player1: { name: player1Name, godCard: player1God },
          player2: { name: player2Name, godCard: player2God }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 1, y: 1 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } }
      ]
    };
  }

  /**
   * 创建自定义移动序列
   */
  static createMoveSequence(moves: Array<{
    type: GameMove['type'];
    player: 'player1' | 'player2';
    from?: Position;
    to: Position;
    metadata?: any;
  }>): GameMove[] {
    return moves.map(move => ({
      type: move.type,
      player: move.player,
      from: move.from,
      to: move.to,
      buildAt: move.metadata?.buildAt,
      metadata: move.metadata
    }));
  }

  /**
   * 生成测试用的随机位置（确保在边界内）
   */
  static generateRandomPosition(maxX: number = 4, maxY: number = 4): Position {
    return {
      x: Math.floor(Math.random() * (maxX + 1)),
      y: Math.floor(Math.random() * (maxY + 1))
    };
  }

  /**
   * 验证位置是否在棋盘边界内
   */
  static isValidPosition(position: Position, boardWidth: number = 5, boardHeight: number = 5): boolean {
    return position.x >= 0 && position.x < boardWidth && 
           position.y >= 0 && position.y < boardHeight;
  }

  /**
   * 计算两个位置之间的曼哈顿距离
   */
  static manhattanDistance(pos1: Position, pos2: Position): number {
    return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
  }

  /**
   * 检查两个位置是否相邻（8方向）
   */
  static areAdjacent(pos1: Position, pos2: Position): boolean {
    const dx = Math.abs(pos1.x - pos2.x);
    const dy = Math.abs(pos1.y - pos2.y);
    return dx <= 1 && dy <= 1 && (dx > 0 || dy > 0);
  }

  /**
   * 获取位置周围的所有相邻位置
   */
  static getAdjacentPositions(position: Position, boardWidth: number = 5, boardHeight: number = 5): Position[] {
    const adjacent: Position[] = [];
    
    for (let dx = -1; dx <= 1; dx++) {
      for (let dy = -1; dy <= 1; dy++) {
        if (dx === 0 && dy === 0) continue;
        
        const newPos = { x: position.x + dx, y: position.y + dy };
        if (this.isValidPosition(newPos, boardWidth, boardHeight)) {
          adjacent.push(newPos);
        }
      }
    }
    
    return adjacent;
  }

  /**
   * 创建神卡特定的测试场景
   */
  static createGodCardScenario(godCard: string, opponentGod: string = 'Demeter'): GameScenario {
    const scenarios: Record<string, Partial<GameScenario>> = {
      Apollo: {
        name: 'Apollo换位测试',
        description: '测试Apollo的位置交换能力',
        moves: [
          { type: 'place_worker', player: 'player1', to: { x: 1, y: 0 }, metadata: { workerIndex: 0 } },
          { type: 'place_worker', player: 'player1', to: { x: 2, y: 1 }, metadata: { workerIndex: 1 } },
          { type: 'place_worker', player: 'player2', to: { x: 1, y: 1 }, metadata: { workerIndex: 0 } },
          { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 1 } },
          { type: 'special_action', player: 'player1', from: { x: 1, y: 0 }, to: { x: 1, y: 1 } }
        ]
      },
      Artemis: {
        name: 'Artemis多重移动测试',
        description: '测试Artemis的双重移动能力',
        moves: [
          { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
          { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 1 } },
          { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 0 } },
          { type: 'place_worker', player: 'player2', to: { x: 1, y: 4 }, metadata: { workerIndex: 1 } },
          { type: 'move', player: 'player1', from: { x: 2, y: 2 }, to: { x: 2, y: 1 } },
          { type: 'special_action', player: 'player1', from: { x: 2, y: 1 }, to: { x: 1, y: 1 } }
        ]
      },
      Demeter: {
        name: 'Demeter双重建造测试',
        description: '测试Demeter的双重建造能力',
        moves: [
          { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
          { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 1 } },
          { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 0 } },
          { type: 'place_worker', player: 'player2', to: { x: 1, y: 4 }, metadata: { workerIndex: 1 } },
          { type: 'move', player: 'player1', from: { x: 2, y: 2 }, to: { x: 2, y: 3 } },
          { type: 'build', player: 'player1', to: { x: 1, y: 3 }, metadata: { buildType: 'block' } },
          { type: 'build', player: 'player1', to: { x: 3, y: 3 }, metadata: { buildType: 'block', isOptional: true } }
        ]
      }
    };

    const baseScenario: GameScenario = {
      id: `${godCard.toLowerCase()}_test`,
      name: scenarios[godCard]?.name || `${godCard}测试`,
      description: scenarios[godCard]?.description || `测试${godCard}神卡功能`,
      setup: {
        players: {
          player1: { name: `${godCard}_Player`, godCard },
          player2: { name: 'Opponent', godCard: opponentGod }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: scenarios[godCard]?.moves || this.createMoveSequence([
        { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 1, y: 1 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } }
      ])
    };

    return baseScenario;
  }

  /**
   * 生成性能测试场景（大量移动）
   */
  static createPerformanceTestScenario(moveCount: number = 20): GameScenario {
    const moves: GameMove[] = [
      // 基础工人放置
      { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
      { type: 'place_worker', player: 'player1', to: { x: 1, y: 0 }, metadata: { workerIndex: 1 } },
      { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 0 } },
      { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } }
    ];

    // 添加随机移动和建造
    for (let i = 0; i < moveCount; i++) {
      const player = i % 2 === 0 ? 'player1' : 'player2';
      const randomPos = this.generateRandomPosition();
      
      if (i % 2 === 0) {
        moves.push({
          type: 'move',
          player,
          from: this.generateRandomPosition(),
          to: randomPos
        });
      } else {
        moves.push({
          type: 'build',
          player,
          to: randomPos,
          metadata: { buildType: 'block' }
        });
      }
    }

    return {
      id: 'performance_test',
      name: '性能测试场景',
      description: `包含${moveCount}次移动的性能测试`,
      setup: {
        players: {
          player1: { name: 'PerfPlayer1', godCard: 'Apollo' },
          player2: { name: 'PerfPlayer2', godCard: 'Demeter' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves
    };
  }

  /**
   * 创建边界条件测试场景
   */
  static createBoundaryTestScenario(): GameScenario {
    return {
      id: 'boundary_test',
      name: '边界条件测试',
      description: '测试棋盘边缘位置的操作',
      setup: {
        players: {
          player1: { name: 'BoundaryPlayer1', godCard: 'Artemis' },
          player2: { name: 'BoundaryPlayer2', godCard: 'Atlas' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        // 在四个角落放置工人
        { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 0, y: 4 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 0 }, metadata: { workerIndex: 1 } }
      ]
    };
  }

  /**
   * 比较两个场景是否相等
   */
  static compareScenarios(scenario1: GameScenario, scenario2: GameScenario): boolean {
    return JSON.stringify(scenario1) === JSON.stringify(scenario2);
  }

  /**
   * 深度克隆场景对象
   */
  static cloneScenario(scenario: GameScenario): GameScenario {
    return JSON.parse(JSON.stringify(scenario));
  }

  /**
   * 合并两个移动序列
   */
  static combineMoveSequences(sequence1: GameMove[], sequence2: GameMove[]): GameMove[] {
    return [...sequence1, ...sequence2];
  }
}

/**
 * 测试数据生成器
 */
export class TestDataGenerator {
  /**
   * 生成随机玩家名称
   */
  static generatePlayerName(prefix: string = 'TestPlayer'): string {
    const randomId = Math.floor(Math.random() * 1000);
    return `${prefix}${randomId}`;
  }

  /**
   * 生成随机神卡组合
   */
  static generateGodCardPair(): [string, string] {
    const gods = ['Apollo', 'Artemis', 'Athena', 'Atlas', 'Demeter', 
                  'Hephaestus', 'Hermes', 'Minotaur', 'Pan', 'Prometheus'];
    
    const shuffled = gods.sort(() => Math.random() - 0.5);
    return [shuffled[0], shuffled[1]];
  }

  /**
   * 生成测试用的移动模式
   */
  static generateMovePattern(patternType: 'linear' | 'diagonal' | 'spiral' | 'random'): Position[] {
    const positions: Position[] = [];
    
    switch (patternType) {
      case 'linear':
        for (let i = 0; i < 5; i++) {
          positions.push({ x: i, y: 2 });
        }
        break;
        
      case 'diagonal':
        for (let i = 0; i < 5; i++) {
          positions.push({ x: i, y: i });
        }
        break;
        
      case 'spiral':
        // 简化的螺旋模式
        positions.push({ x: 2, y: 2 });
        positions.push({ x: 2, y: 1 });
        positions.push({ x: 3, y: 1 });
        positions.push({ x: 3, y: 2 });
        positions.push({ x: 3, y: 3 });
        break;
        
      case 'random':
        for (let i = 0; i < 8; i++) {
          positions.push(TestHelpers.generateRandomPosition());
        }
        break;
    }
    
    return positions;
  }
}