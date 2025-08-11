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

  test('Execute Apollo vs Hermes scenario with detailed validations', async ({ page }) => {
    // 重置测试引擎状态
    engine.resetEngine();
    
    // 加载场景
    const scenario = await ScenarioLoader.loadScenario('apollo-vs-hermes.yaml');
    
    // 使用新的分步验证方法
    await engine.executeScenarioWithStepValidations(scenario, {
      4: async () => {
        // 工人放置完成后验证
        await test.step('验证初始工人放置', async () => {
          const gameState = await engine.getCurrentGameState();
          expect(gameState.workerPositions.player1.length).toBe(2);
          expect(gameState.workerPositions.player2.length).toBe(2);
          console.log('✅ 初始工人放置验证完成');
        });
      },
      5: async () => {
        // Apollo换位步骤后立即验证
        await test.step('验证Apollo换位能力使用', async () => {
          // 等待一点时间确保状态快照已捕获
          await new Promise(resolve => setTimeout(resolve, 100));
          const swapResult = await engine.validateApolloSwapUsingStateComparison(4, 5);
          expect(swapResult).toBe(true);
          console.log('✅ Apollo换位能力实时验证完成');
        });
      },
      7: async () => {
        // Hermes第一次特殊移动后验证
        await test.step('验证Hermes特殊移动能力', async () => {
          await new Promise(resolve => setTimeout(resolve, 100));
          // 对比第5步（Apollo换位后，Hermes被换到(1,1)）和第7步（Hermes从(1,1)移动到(1,4)）
          const enhancedMoveResult = await engine.validateHermesEnhancedMoveUsingStateComparison(5, 7);
          expect(enhancedMoveResult).toBe(true);
          console.log('✅ Hermes特殊移动能力实时验证完成');
        });
      },
      11: async () => {
        // Hermes第二次特殊移动后验证
        await test.step('验证Hermes第二次特殊移动', async () => {
          await new Promise(resolve => setTimeout(resolve, 100));
          // 对比第9步（Apollo移动后，Hermes第二个工人还在(1,3)）和第11步（Hermes从(1,3)移动到(3,3)）
          const enhancedMoveResult = await engine.validateHermesEnhancedMoveUsingStateComparison(9, 11);
          expect(enhancedMoveResult).toBe(true);
          console.log('✅ Hermes第二次特殊移动验证完成');
        });
      }
    });
    
    // 获取最终游戏状态
    const gameState = await engine.getCurrentGameState();
    expect(gameState.status).toBeDefined();
    expect(gameState.boardState).toBeDefined();
    expect(gameState.workerPositions).toBeDefined();
    expect(gameState.buildingCounts).toBeDefined();
    
    // 根据YAML中的expectedOutcome进行最终验证
    await test.step('验证游戏仍在进行中', async () => {
      await pageManager.gameBoardPage.verifyGameInProgress();
      // 验证没有胜利消息显示
      await expect(pageManager.gameBoardPage.winnerMessage).not.toBeVisible();
    });

    await test.step('验证最终游戏状态', async () => {
      // 验证基本的工人数量和棋盘结构
      expect(gameState.workerPositions.player1.length).toBe(2);
      expect(gameState.workerPositions.player2.length).toBe(2);
      
      // 验证网格基本结构
      const gridButtons = pageManager.gameBoardPage.gridButtons;
      const buttonCount = await gridButtons.count();
      expect(buttonCount).toBeGreaterThanOrEqual(25); // 5x5网格
      
      console.log('✅ 最终游戏状态验证完成');
      console.log('实际工人位置:', gameState.workerPositions);
      console.log('建筑物统计:', gameState.buildingCounts);
    });

    await test.step('验证状态快照功能', async () => {
      // 验证状态快照是否正确捕获
      const initialSnapshot = engine.getStateSnapshot(0);
      const swapSnapshot = engine.getStateSnapshot(5);
      const moveSnapshot = engine.getStateSnapshot(7);
      
      expect(initialSnapshot).toBeDefined();
      expect(swapSnapshot).toBeDefined();
      expect(moveSnapshot).toBeDefined();
      
      console.log('✅ 状态快照功能验证完成');
      console.log('捕获的快照数量:', [initialSnapshot, swapSnapshot, moveSnapshot].filter(Boolean).length);
    });
  });

  // test('Load and execute multiple scenarios by tags', async ({ page }) => {
  //   // 根据标签加载相关场景
  //   const apolloScenarios = await ScenarioLoader.loadScenariosByTags(['apollo']);
    
  //   expect(apolloScenarios.length).toBeGreaterThan(0);
    
  //   for (const scenario of apolloScenarios.slice(0, 2)) { // 限制执行数量
  //     await test.step(`Execute ${scenario.name}`, async () => {
  //       await engine.executeScenario(scenario);
  //       await pageManager.gameBoardPage.verifyPageLoaded();
  //     });
  //   }
  // });

  // test('Load scenarios by difficulty level', async ({ page }) => {
  //   // 加载基础难度的场景
  //   const basicScenarios = await ScenarioLoader.loadScenariosByDifficulty('basic');
    
  //   expect(basicScenarios.length).toBeGreaterThan(0);
    
  //   // 执行第一个基础场景
  //   if (basicScenarios.length > 0) {
  //     await engine.executeScenario(basicScenarios[0]);
  //     await pageManager.gameBoardPage.verifyGameInProgress();
  //   }
  // });

  // test('Generate and execute scenario variant', async ({ page }) => {
  //   // 加载基础场景
  //   const baseScenario = await ScenarioLoader.loadScenario('apollo-swap.json');
    
  //   // 生成变体：将Bob的神卡改为Pan
  //   const variant = ScenarioManager.generateScenarioVariant(baseScenario, {
  //     id: 'apollo_vs_pan_variant',
  //     name: 'Apollo vs Pan 变体测试',
  //     setup: {
  //       players: {
  //         player1: baseScenario.setup.players.player1,
  //         player2: { name: 'Pan_Bob', godCard: 'Pan' }
  //       }
  //     }
  //   });
    
  //   // 执行变体场景
  //   await engine.executeScenario(variant);
  //   await pageManager.gameBoardPage.verifyGameInProgress();
  // });

  // test('Batch execute scenario suite', async ({ page }) => {
  //   // 定义要执行的场景文件列表
  //   const scenarioFiles = [
  //     // 'apollo-swap.json',
  //     'demeter-double-build.yaml'
  //   ];
    
  //   // 批量执行
  //   const results = await engine.executeScenarioSuite(scenarioFiles);
    
  //   // 验证执行结果
  //   console.log('执行结果:', results);
  //   expect(results.passed.length).toBeGreaterThan(0);
    
  //   // 如果有失败的场景，输出详细信息
  //   if (results.failed.length > 0) {
  //     console.log('失败的场景:', results.failed);
  //   }
  // });

  // test('Validate scenario data integrity', async () => {
  //   // 加载所有场景并验证数据完整性
  //   const allScenarios = await ScenarioLoader.loadAllScenarios();
  //   const scenarioCount = Object.keys(allScenarios).length;
    
  //   expect(scenarioCount).toBeGreaterThan(0);
    
  //   // 验证每个场景的数据格式
  //   for (const [id, scenario] of Object.entries(allScenarios)) {
  //     expect(scenario.id).toBe(id);
  //     expect(scenario.name).toBeDefined();
  //     expect(scenario.setup).toBeDefined();
  //     expect(scenario.moves).toBeInstanceOf(Array);
  //     expect(scenario.moves.length).toBeGreaterThan(0);
  //   }
  // });

  // test('Get scenario statistics', async () => {
  //   // 获取场景统计信息
  //   const stats = await ScenarioLoader.getScenarioStats();
    
  //   console.log('场景统计:', stats);
    
  //   expect(stats.total).toBeGreaterThan(0);
  //   expect(stats.byDifficulty).toBeDefined();
  //   expect(stats.byGodCard).toBeDefined();
  //   expect(stats.byTags).toBeDefined();
  // });

  // test('Save custom scenario to file', async () => {
  //   // 创建自定义场景
  //   const customScenario = {
  //     id: 'custom_artemis_test',
  //     name: '自定义Artemis测试',
  //     description: '测试Artemis双重移动能力的自定义场景',
  //     setup: {
  //       players: {
  //         player1: { name: 'Artemis_Player', godCard: 'Artemis' },
  //         player2: { name: 'Atlas_Player', godCard: 'Atlas' }
  //       },
  //       boardSize: { width: 5, height: 5 }
  //     },
  //     moves: [
  //       { type: 'place_worker' as const, player: 'player1' as const, to: { x: 2, y: 2 }, metadata: { workerIndex: 0 } },
  //       { type: 'place_worker' as const, player: 'player1' as const, to: { x: 0, y: 0 }, metadata: { workerIndex: 1 } },
  //       { type: 'place_worker' as const, player: 'player2' as const, to: { x: 4, y: 4 }, metadata: { workerIndex: 0 } },
  //       { type: 'place_worker' as const, player: 'player2' as const, to: { x: 1, y: 4 }, metadata: { workerIndex: 1 } }
  //     ]
  //   };
    
  //   // 保存为YAML格式
  //   await ScenarioLoader.saveScenario(customScenario, 'custom-artemis-test.yaml', 'yaml');
    
  //   // 验证可以重新加载
  //   // const loadedScenario = await ScenarioLoader.loadScenario('custom-artemis-test.json');
  //   // expect(loadedScenario.id).toBe(customScenario.id);
  //   // expect(loadedScenario.name).toBe(customScenario.name);
  // });



  // test('Performance test with large scenario set', async ({ page }) => {
  //   const startTime = Date.now();
    
  //   // 加载所有场景
  //   const allScenarios = await ScenarioLoader.loadAllScenarios();
  //   const loadTime = Date.now() - startTime;
    
  //   console.log(`加载${Object.keys(allScenarios).length}个场景耗时: ${loadTime}ms`);
    
  //   // 验证加载性能合理（应该在合理时间内完成）
  //   expect(loadTime).toBeLessThan(5000); // 5秒内
    
  //   // 清除缓存后重新加载，测试缓存效果
  //   ScenarioLoader.clearCache();
    
  //   const recacheStartTime = Date.now();
  //   await ScenarioLoader.loadAllScenarios();
  //   const recacheTime = Date.now() - recacheStartTime;
    
  //   console.log(`重新缓存耗时: ${recacheTime}ms`);
    
  //   // 再次加载，应该使用缓存，速度更快
  //   const cachedStartTime = Date.now();
  //   await ScenarioLoader.loadAllScenarios();
  //   const cachedTime = Date.now() - cachedStartTime;
    
  //   console.log(`缓存加载耗时: ${cachedTime}ms`);
  //   expect(cachedTime).toBeLessThan(recacheTime);
  // });
});

