// 棋谱数据格式定义

export interface GameMove {
  type: 'place_worker' | 'move' | 'build' | 'special_action';
  player: 'player1' | 'player2';
  step?: number; // 步骤编号（用于分步执行）
  from?: Position;
  to: Position;
  buildAt?: Position;
  metadata?: {
    workerIndex?: number;
    buildType?: 'block' | 'dome';
    isOptional?: boolean;
    description?: string;
    specialAbility?: string;
    [key: string]: any; // 允许其他自定义属性
  };
}

export interface Position {
  x: number;
  y: number;
}

export interface GameSetup {
  players: {
    player1: { name: string; godCard: string };
    player2: { name: string; godCard: string };
  };
  boardSize: { width: number; height: number };
}

export interface GameScenario {
  id: string;
  name: string;
  description: string;
  setup: GameSetup;
  moves: GameMove[];
  expectedOutcome?: {
    winner?: 'player1' | 'player2';
    gameState?: 'in_progress' | 'ended';
    boardState?: BoardCell[][];
  };
}

export interface BoardCell {
  level: number;
  worker?: {
    player: 'player1' | 'player2';
    index: number;
  };
  hasDome?: boolean;
}

// 预定义的测试场景
export const testScenarios: Record<string, GameScenario> = {
  basicWorkerPlacement: {
    id: 'basic_worker_placement',
    name: '基础工人放置',
    description: '测试游戏开始时的工人放置流程',
    setup: {
      players: {
        player1: { name: 'Alice', godCard: 'Demeter' },
        player2: { name: 'Bob', godCard: 'Pan' }
      },
      boardSize: { width: 5, height: 5 }
    },
    moves: [
      { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
      { type: 'place_worker', player: 'player1', to: { x: 1, y: 1 }, metadata: { workerIndex: 1 } },
      { type: 'place_worker', player: 'player2', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
      { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 1 } }
    ],
    expectedOutcome: {
      gameState: 'in_progress'
    }
  },

  apolloSwapMove: {
    id: 'apollo_swap_move',
    name: 'Apollo换位能力测试',
    description: '测试Apollo神卡的换位特殊能力',
    setup: {
      players: {
        player1: { name: 'Alice', godCard: 'Apollo' },
        player2: { name: 'Bob', godCard: 'Demeter' }
      },
      boardSize: { width: 5, height: 5 }
    },
    moves: [
      // 工人放置阶段
      { type: 'place_worker', player: 'player1', to: { x: 1, y: 0 }, metadata: { workerIndex: 0 } },
      { type: 'place_worker', player: 'player1', to: { x: 2, y: 1 }, metadata: { workerIndex: 1 } },
      { type: 'place_worker', player: 'player2', to: { x: 1, y: 1 }, metadata: { workerIndex: 0 } },
      { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 1 } },
      // Apollo换位移动
      { type: 'move', player: 'player1', from: { x: 1, y: 0 }, to: { x: 1, y: 1 }, metadata: { workerIndex: 0 } }
    ]
  },

  demeterDoubleBuild: {
    id: 'demeter_double_build',
    name: 'Demeter双重建造测试',
    description: '测试Demeter神卡的可选二次建造能力',
    setup: {
      players: {
        player1: { name: 'Alice', godCard: 'Demeter' },
        player2: { name: 'Bob', godCard: 'Pan' }
      },
      boardSize: { width: 5, height: 5 }
    },
    moves: [
      // 工人放置和基本移动省略...
      { type: 'build', player: 'player1', to: { x: 0, y: 0 }, metadata: { buildType: 'block' } },
      { type: 'build', player: 'player1', to: { x: 0, y: 1 }, metadata: { buildType: 'block', isOptional: true } }
    ]
  },

  panWinCondition: {
    id: 'pan_win_condition',
    name: 'Pan胜利条件测试',
    description: '测试Pan神卡通过下降两层或更多获胜的特殊胜利条件',
    setup: {
      players: {
        player1: { name: 'Alice', godCard: 'Pan' },
        player2: { name: 'Bob', godCard: 'Demeter' }
      },
      boardSize: { width: 5, height: 5 }
    },
    moves: [
      // 这需要预先构建一个特定的棋盘状态
      { type: 'move', player: 'player1', from: { x: 1, y: 1 }, to: { x: 0, y: 0 } }
    ],
    expectedOutcome: {
      winner: 'player1',
      gameState: 'ended'
    }
  }
};

// 棋谱验证函数
export function validateGameScenario(scenario: GameScenario): string[] {
  const errors: string[] = [];
  
  // 验证玩家设置
  if (!scenario.setup.players.player1.name || !scenario.setup.players.player2.name) {
    errors.push('Player names are required');
  }
  
  // 验证移动序列
  scenario.moves.forEach((move, index) => {
    if (move.to.x < 0 || move.to.x >= scenario.setup.boardSize.width ||
        move.to.y < 0 || move.to.y >= scenario.setup.boardSize.height) {
      errors.push(`Move ${index}: Invalid position (${move.to.x}, ${move.to.y})`);
    }
    
    if (move.type === 'move' && !move.from) {
      errors.push(`Move ${index}: Move action requires 'from' position`);
    }
  });
  
  return errors;
}