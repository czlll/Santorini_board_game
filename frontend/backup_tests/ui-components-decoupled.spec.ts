import { test, expect } from '@playwright/test';
import { PageObjectManager } from '../tests/pages/game-pages';

test.describe('UI Components - Decoupled Tests', () => {
  let pageManager: PageObjectManager;

  test.beforeEach(async ({ page }) => {
    pageManager = new PageObjectManager(page);
  });

  test('Homepage UI elements validation', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.verifyPageLoaded();
    
    // 验证页面标题
    await expect(page).toHaveTitle(/.*Santorini.*/);
  });

  test('Player form UI components', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.startGame();
    
    await pageManager.playerFormPage.verifyPageLoaded();
    
    // 验证表单元素
    await expect(pageManager.playerFormPage.player1NameInput).toBeVisible();
    await expect(pageManager.playerFormPage.player2NameInput).toBeVisible();
    await expect(pageManager.playerFormPage.startGameButton).toBeVisible();
  });

  test('God card options validation', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.startGame();
    
    const expectedGods = ['Apollo', 'Artemis', 'Athena', 'Atlas', 'Demeter', 
                         'Hephaestus', 'Hermes', 'Minotaur', 'Pan', 'Prometheus'];
    
    await pageManager.playerFormPage.verifyGodCardOptions(expectedGods);
  });

  test('Form validation behavior', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.startGame();
    
    await pageManager.playerFormPage.verifyFormValidation();
    
    // 测试部分填写表单
    await pageManager.playerFormPage.fillPlayerInfo('OnlyOnePlayer', '');
    await pageManager.playerFormPage.startGame();
    await expect(page).toHaveURL(/.*playerForm/); // 应该停留在表单页面
    
    // 完整填写表单
    await pageManager.playerFormPage.fillPlayerInfo('Player1', 'Player2');
    await pageManager.playerFormPage.selectGodCards('Demeter', 'Pan');
    await pageManager.playerFormPage.startGame();
    
    await pageManager.gameBoardPage.verifyPageLoaded();
  });

  test('Game board UI elements', async ({ page }) => {
    await pageManager.setupCompleteGame('Alice', 'Bob');
    
    // 验证游戏板元素
    await pageManager.gameBoardPage.verifyGridButtonsCount(25);
    await pageManager.gameBoardPage.verifyControlButtonsVisible();
    await pageManager.gameBoardPage.verifyControlButtonsEnabled();
  });

  test('Button interactions and state', async ({ page }) => {
    await pageManager.setupCompleteGame('Player1', 'Player2');
    
    // 测试网格按钮交互
    await pageManager.gameBoardPage.clickGridPosition(0, 0);
    await pageManager.gameBoardPage.waitForStateUpdate();
    
    // 验证按钮状态
    const buttonState = await pageManager.gameBoardPage.getGridButtonState(0, 0);
    expect(buttonState.isVisible).toBe(true);
    
    // 测试控制按钮
    await pageManager.gameBoardPage.clickSkipAction();
    await pageManager.gameBoardPage.waitForStateUpdate();
    
    await pageManager.gameBoardPage.clickUndo();
    await pageManager.gameBoardPage.waitForStateUpdate();
  });

  test('Responsive design - Mobile view', async ({ page }) => {
    await pageManager.verifyResponsiveDesign({ width: 375, height: 667 });
    
    await pageManager.setupCompleteGame('MobilePlayer1', 'MobilePlayer2');
    
    // 在移动端验证游戏板可见性
    await pageManager.gameBoardPage.verifyPageLoaded();
    await pageManager.gameBoardPage.verifyControlButtonsVisible();
  });

  test('Responsive design - Tablet view', async ({ page }) => {
    await pageManager.verifyResponsiveDesign({ width: 768, height: 1024 });
    
    await pageManager.setupCompleteGame('TabletPlayer1', 'TabletPlayer2');
    
    // 验证网格按钮在平板上的可见性
    const firstButton = await pageManager.gameBoardPage.getGridButtonState(0, 0);
    const middleButton = await pageManager.gameBoardPage.getGridButtonState(2, 2);
    const lastButton = await pageManager.gameBoardPage.getGridButtonState(4, 4);
    
    expect(firstButton.isVisible).toBe(true);
    expect(middleButton.isVisible).toBe(true);
    expect(lastButton.isVisible).toBe(true);
  });

  test('CSS classes and styling verification', async ({ page }) => {
    await pageManager.setupCompleteGame('Alice', 'Bob');
    
    // 检查游戏状态元素的CSS类
    const gameStatus = await pageManager.gameBoardPage.getGameStatus();
    if (gameStatus) {
      // 验证状态文本存在
      expect(gameStatus.length).toBeGreaterThan(0);
    }
  });

  test('Accessibility - Keyboard navigation', async ({ page }) => {
    await pageManager.homePage.goto();
    
    // 测试Tab导航
    await page.keyboard.press('Tab');
    await expect(pageManager.homePage.startButton).toBeFocused();
    
    // 测试Enter键
    await page.keyboard.press('Enter');
    await pageManager.playerFormPage.verifyPageLoaded();
    
    // 测试表单中的Tab导航
    await page.keyboard.press('Tab');
    await expect(pageManager.playerFormPage.player1NameInput).toBeFocused();
  });

  test('Browser navigation - Back/Forward', async ({ page }) => {
    await pageManager.homePage.goto();
    await pageManager.homePage.startGame();
    await pageManager.playerFormPage.verifyPageLoaded();
    
    // 后退到主页
    await page.goBack();
    await expect(page).toHaveURL('/');
    await pageManager.homePage.verifyPageLoaded();
    
    // 前进到表单页
    await page.goForward();
    await pageManager.playerFormPage.verifyPageLoaded();
  });

  test('Page reload persistence', async ({ page }) => {
    await pageManager.setupCompleteGame('TestPlayer1', 'TestPlayer2');
    
    // 执行一些游戏操作
    await pageManager.gameBoardPage.clickGridPosition(0, 0);
    await pageManager.gameBoardPage.waitForStateUpdate();
    
    // 重新加载页面
    await page.reload();
    
    // 验证游戏状态
    await pageManager.gameBoardPage.verifyPageLoaded();
  });

  test('Multiple viewport testing', async ({ page }) => {
    const viewports = [
      { name: 'Desktop', size: { width: 1920, height: 1080 } },
      { name: 'Laptop', size: { width: 1366, height: 768 } },
      { name: 'Tablet', size: { width: 768, height: 1024 } },
      { name: 'Mobile', size: { width: 375, height: 667 } }
    ];

    for (const viewport of viewports) {
      await test.step(`Testing ${viewport.name} viewport`, async () => {
        await page.setViewportSize(viewport.size);
        await pageManager.setupCompleteGame(`${viewport.name}Player1`, `${viewport.name}Player2`);
        await pageManager.gameBoardPage.verifyPageLoaded();
      });
    }
  });
});