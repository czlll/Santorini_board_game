import { test, expect } from '@playwright/test';
import { SantoriniTestEngine } from '../engine/game-engine';
import { PageObjectManager } from '../pages/game-pages';
import { testScenarios, validateGameScenario } from '../data/game-notation';

test.describe('Santorini Game Flow - Decoupled Tests', () => {
  let engine: SantoriniTestEngine;
  let pageManager: PageObjectManager;

  test.beforeEach(async ({ page }) => {
    engine = new SantoriniTestEngine(page);
    pageManager = new PageObjectManager(page);
  });

  test('Execute basic worker placement scenario', async ({ page }) => {
    const scenario = testScenarios.basicWorkerPlacement;
    
    // 验证棋谱数据有效性
    const validationErrors = validateGameScenario(scenario);
    expect(validationErrors).toHaveLength(0);
    
    // 执行场景
    await engine.executeScenario(scenario);
    
    // 使用页面对象验证结果
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Execute Apollo swap move scenario', async ({ page }) => {
    const scenario = testScenarios.apolloSwapMove;
    
    // 验证棋谱
    const validationErrors = validateGameScenario(scenario);
    expect(validationErrors).toHaveLength(0);
    
    // 执行Apollo换位场景
    await engine.executeScenario(scenario);
    
    // 验证游戏状态
    const gameState = await engine.getCurrentGameState();
    expect(gameState.status).toBeDefined();
  });

  test('Execute Demeter double build scenario', async ({ page }) => {
    const scenario = testScenarios.demeterDoubleBuild;
    
    await engine.executeScenario(scenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Execute Pan win condition scenario', async ({ page }) => {
    const scenario = testScenarios.panWinCondition;
    
    await engine.executeScenario(scenario);
    
    // 注意: 这个测试可能需要更复杂的棋盘设置才能实际触发Pan的胜利条件
    // 在实际实现中，需要先构建足够高度的塔
  });

  test('Data-driven test with multiple scenarios', async ({ page }) => {
    // 批量执行多个场景
    const scenariosToTest = [
      testScenarios.basicWorkerPlacement,
      testScenarios.apolloSwapMove
    ];

    for (const scenario of scenariosToTest) {
      await test.step(`Execute scenario: ${scenario.name}`, async () => {
        // 每个场景重新开始
        await engine.executeScenario(scenario);
        
        // 验证基本游戏状态
        await pageManager.gameBoardPage.verifyPageLoaded();
      });
    }
  });

  test('Error handling with invalid scenario', async ({ page }) => {
    // 创建一个无效的场景来测试错误处理
    const invalidScenario = {
      ...testScenarios.basicWorkerPlacement,
      moves: [
        {
          type: 'place_worker' as const,
          player: 'player1' as const,
          to: { x: 10, y: 10 }, // 超出边界的位置
          metadata: { workerIndex: 0 }
        }
      ]
    };

    const validationErrors = validateGameScenario(invalidScenario);
    expect(validationErrors.length).toBeGreaterThan(0);
    expect(validationErrors[0]).toContain('Invalid position');
  });

  test('Custom scenario execution', async ({ page }) => {
    // 动态创建自定义场景
    const customScenario = {
      id: 'custom_test',
      name: '自定义测试场景',
      description: '测试自定义场景执行',
      setup: {
        players: {
          player1: { name: 'CustomAlice', godCard: 'Artemis' },
          player2: { name: 'CustomBob', godCard: 'Atlas' }
        },
        boardSize: { width: 5, height: 5 }
      },
      moves: [
        { type: 'place_worker' as const, player: 'player1' as const, to: { x: 0, y: 0 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker' as const, player: 'player1' as const, to: { x: 4, y: 4 }, metadata: { workerIndex: 1 } },
        { type: 'place_worker' as const, player: 'player2' as const, to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
        { type: 'place_worker' as const, player: 'player2' as const, to: { x: 1, y: 3 }, metadata: { workerIndex: 1 } }
      ]
    };

    await engine.executeScenario(customScenario);
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Scenario with control actions', async ({ page }) => {
    // 测试包含控制动作的场景
    await engine.executeScenario(testScenarios.basicWorkerPlacement);
    
    // 执行控制动作
    await engine.executeControlAction('undo');
    await pageManager.gameBoardPage.waitForStateUpdate();
    
    await engine.executeControlAction('skip');
    await pageManager.gameBoardPage.waitForStateUpdate();
    
    // 验证游戏仍在进行
    await pageManager.gameBoardPage.verifyGameInProgress();
  });

  test('Screenshot capture during scenario execution', async ({ page }) => {
    const scenario = testScenarios.basicWorkerPlacement;
    
    // 在关键步骤截图
    await pageManager.homePage.goto();
    await engine.captureGameState('01-homepage');
    
    await pageManager.homePage.startGame();
    await engine.captureGameState('02-player-form');
    
    await engine.executeScenario(scenario);
    await engine.captureGameState('03-game-board');
  });
});

// 辅助测试工具
test.describe('Scenario Validation Tools', () => {
  test('Validate all predefined scenarios', async () => {
    for (const [key, scenario] of Object.entries(testScenarios)) {
      const errors = validateGameScenario(scenario);
      expect(errors, `Scenario ${key} should be valid`).toHaveLength(0);
    }
  });
  
  test('Scenario serialization and deserialization', async () => {
    const scenario = testScenarios.basicWorkerPlacement;
    
    // 序列化
    const serialized = JSON.stringify(scenario);
    expect(serialized).toBeDefined();
    
    // 反序列化
    const deserialized = JSON.parse(serialized);
    expect(deserialized).toEqual(scenario);
    
    // 验证反序列化后的数据仍然有效
    const errors = validateGameScenario(deserialized);
    expect(errors).toHaveLength(0);
  });
});