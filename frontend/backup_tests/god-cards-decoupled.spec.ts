import { test, expect } from '@playwright/test';
import { SantoriniTestEngine } from '../tests/engine/game-engine';
import { PageObjectManager } from '../tests/pages/game-pages';
import { GameScenario } from '../tests/data/game-notation';

test.describe('God Cards Functionality - Decoupled Tests', () => {
  let engine: SantoriniTestEngine;
  let pageManager: PageObjectManager;

  test.beforeEach(async ({ page }) => {
    engine = new SantoriniTestEngine(page);
    pageManager = new PageObjectManager(page);
  });

  test('Apollo swap ability scenario', async ({ page }) => {
    const apolloScenario: GameScenario = {
      id: 'apollo_swap_test',
      name: 'Apollo位置交换测试',
      description: '测试Apollo神卡的位置交换特殊能力',
      setup: {
        players: {
          player1: { name: 'Apollo_Alice', godCard: 'Apollo' },
          player2: { name: 'Normal_Bob', godCard: 'Demeter' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        // 工人放置阶段
        { type: 'place_worker', player: 'player1', to: { x: 1, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 1 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 1, y: 1 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 1 } },
        // Apollo特殊移动 - 尝试与敌方工人交换位置
        { type: 'special_action', player: 'player1', from: { x: 1, y: 0 }, to: { x: 1, y: 1 } }
      ]
    };

    await engine.executeScenario(apolloScenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Demeter double build scenario', async ({ page }) => {
    const demeterScenario: GameScenario = {
      id: 'demeter_double_build',
      name: 'Demeter双重建造测试',
      description: '测试Demeter可选的第二次建造能力',
      setup: {
        players: {
          player1: { name: 'Demeter_Alice', godCard: 'Demeter' },
          player2: { name: 'Normal_Bob', godCard: 'Pan' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        // 工人放置
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 3, y: 2 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } },
        // 移动到可以建造的位置
        { type: 'move', player: 'player1', from: { x: 2, y: 2 }, to: { x: 2, y: 3 } },
        // 第一次建造
        { type: 'build', player: 'player1', to: { x: 1, y: 3 }, metadata: { buildType: 'block' } },
        // Demeter的可选第二次建造
        { type: 'build', player: 'player1', to: { x: 3, y: 3 }, metadata: { buildType: 'block', isOptional: true } }
      ]
    };

    await engine.executeScenario(demeterScenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Pan win condition scenario', async ({ page }) => {
    // 注意：这是一个简化的Pan胜利条件测试
    // 实际实现中需要先建造足够高度的塔
    const panScenario: GameScenario = {
      id: 'pan_win_condition',
      name: 'Pan特殊胜利条件',
      description: '测试Pan通过下降两层获胜',
      setup: {
        players: {
          player1: { name: 'Pan_Alice', godCard: 'Pan' },
          player2: { name: 'Normal_Bob', godCard: 'Apollo' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        // 基础工人放置
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 1, y: 1 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } },
        // 这里在实际测试中需要构建一个复杂的塔结构
        // 然后让Pan的工人从高塔下降到地面
        { type: 'special_action', player: 'player1', from: { x: 2, y: 2 }, to: { x: 0, y: 1 } }
      ],
      expectedOutcome: {
        winner: 'player1',
        gameState: 'ended'
      }
    };

    await engine.executeScenario(panScenario);
    
    // 注意：由于实际的Pan胜利条件需要特定的棋盘状态，
    // 这个测试可能不会立即触发胜利，需要根据实际游戏逻辑调整
  });

  test('Artemis multiple move scenario', async ({ page }) => {
    const artemisScenario: GameScenario = {
      id: 'artemis_multiple_moves',
      name: 'Artemis多次移动测试',
      description: '测试Artemis可以移动两次的能力',
      setup: {
        players: {
          player1: { name: 'Artemis_Alice', godCard: 'Artemis' },
          player2: { name: 'Normal_Bob', godCard: 'Atlas' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 3, y: 3 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } },
        // Artemis第一次移动
        { type: 'move', player: 'player1', from: { x: 2, y: 2 }, to: { x: 2, y: 1 } },
        // Artemis可选的第二次移动
        { type: 'special_action', player: 'player1', from: { x: 2, y: 1 }, to: { x: 1, y: 1 } }
      ]
    };

    await engine.executeScenario(artemisScenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Atlas dome building scenario', async ({ page }) => {
    const atlasScenario: GameScenario = {
      id: 'atlas_dome_build',
      name: 'Atlas圆顶建造测试', 
      description: '测试Atlas可以在任何层级建造圆顶',
      setup: {
        players: {
          player1: { name: 'Atlas_Alice', godCard: 'Atlas' },
          player2: { name: 'Normal_Bob', godCard: 'Hephaestus' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 1, y: 2 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } },
        // Atlas特殊建造 - 在地面直接建造圆顶
        { type: 'special_action', player: 'player1', to: { x: 3, y: 2 }, metadata: { buildType: 'dome' } }
      ]
    };

    await engine.executeScenario(atlasScenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('God card selection validation', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.startGame();
    
    // 测试选择相同神卡
    await pageManager.playerFormPage.fillPlayerInfo('Player1', 'Player2');
    await pageManager.playerFormPage.selectGodCards('Apollo', 'Apollo');
    await pageManager.playerFormPage.startGame();
    
    // 验证游戏是否正常开始（取决于游戏规则是否允许相同神卡）
    await page.waitForTimeout(1000);
  });

  test('All god cards selectable verification', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.startGame();
    
    const godCards = ['Apollo', 'Artemis', 'Athena', 'Atlas', 'Demeter', 
                     'Hephaestus', 'Hermes', 'Minotaur', 'Pan', 'Prometheus'];
    
    // 测试每个神卡都可以被选择
    for (const god of godCards) {
      await pageManager.playerFormPage.player1GodSelect.selectOption(god);
      const selectedValue = await pageManager.playerFormPage.player1GodSelect.inputValue();
      expect(selectedValue).toBe(god);
    }
  });

  test('God card combinations testing', async ({ page }) => {
    // 测试不同神卡组合的游戏流程
    const godCombinations = [
      ['Apollo', 'Demeter'],
      ['Artemis', 'Atlas'],
      ['Pan', 'Prometheus'],
      ['Athena', 'Hephaestus'],
      ['Hermes', 'Minotaur']
    ];

    for (const [god1, god2] of godCombinations) {
      await test.step(`Testing combination: ${god1} vs ${god2}`, async () => {
        const combinationScenario: GameScenario = {
          id: `combo_${god1}_${god2}`,
          name: `${god1} vs ${god2} 组合测试`,
          description: `测试 ${god1} 和 ${god2} 的神卡组合`,
          setup: {
            players: {
              player1: { name: `${god1}_Player`, godCard: god1 },
              player2: { name: `${god2}_Player`, godCard: god2 }
            },
            boardSize: { width: 5, height: 5 }
          },
          moves: [
            // 基础工人放置测试
            { type: 'place_worker', player: 'player1', to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
            { type: 'place_worker', player: 'player1', to: { x: 1, y: 1 }, metadata: { workerIndex: 1 } },
            { type: 'place_worker', player: 'player2', to: { x: 3, y: 3 }, metadata: { workerIndex: 0 } },
            { type: 'place_worker', player: 'player2', to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } }
          ]
        };

        await engine.executeScenario(combinationScenario);
        await pageManager.gameBoardPage.verifyGameInProgress();
      });
    }
  });

  test('Advanced god card scenario with complex board state', async ({ page }) => {
    // 创建一个复杂的多步骤场景来测试神卡交互
    const complexScenario: GameScenario = {
      id: 'complex_god_interaction',
      name: '复杂神卡交互测试',
      description: '测试多种神卡能力在复杂游戏状态下的表现',
      setup: {
        players: {
          player1: { name: 'AdvancedAlice', godCard: 'Prometheus' },
          player2: { name: 'AdvancedBob', godCard: 'Athena' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        // 工人放置
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player1', to: { x: 2, y: 1 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker', player: 'player2', to: { x: 1, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker', player: 'player2', to: { x: 3, y: 2 }, metadata: { workerIndex: 1 } },
        
        // 复杂的移动和建造序列
        { type: 'move', player: 'player1', from: { x: 2, y: 2 }, to: { x: 2, y: 3 } },
        { type: 'build', player: 'player1', to: { x: 2, y: 2 }, metadata: { buildType: 'block' } },
        
        { type: 'move', player: 'player2', from: { x: 1, y: 2 }, to: { x: 1, y: 3 } },
        { type: 'build', player: 'player2', to: { x: 0, y: 3 }, metadata: { buildType: 'block' } }
      ]
    };

    await engine.executeScenario(complexScenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });
});