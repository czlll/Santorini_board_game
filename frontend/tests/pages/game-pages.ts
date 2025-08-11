import { Page, Locator, expect } from '@playwright/test';

/**
 * 主页页面对象
 */
export class HomePage {
  readonly page: Page;
  readonly startButton: Locator;
  readonly gameTitle: Locator;

  constructor(page: Page) {
    this.page = page;
    this.startButton = page.locator('button:has-text("Click Me to Start")');
    this.gameTitle = page.locator('h1');
  }

  async goto() {
    await this.page.goto('/');
  }

  async startGame() {
    await this.startButton.click();
  }

  async verifyPageLoaded() {
    await expect(this.startButton).toBeVisible();
    await expect(this.page).toHaveTitle(/.*Santorini.*/);
  }
}

/**
 * 玩家表单页面对象
 */
export class PlayerFormPage {
  readonly page: Page;
  readonly player1NameInput: Locator;
  readonly player2NameInput: Locator;
  readonly player1GodSelect: Locator;
  readonly player2GodSelect: Locator;
  readonly startGameButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.player1NameInput = page.locator('input[placeholder="please enter your player name"]').nth(0);
    this.player2NameInput = page.locator('input[placeholder="please enter your player name"]').nth(1);
    this.player1GodSelect = page.locator('select').nth(0);
    this.player2GodSelect = page.locator('select').nth(1);
    this.startGameButton = page.locator('button:has-text("Start Game")');
  }

  async fillPlayerInfo(player1Name: string, player2Name: string) {
    await this.player1NameInput.fill(player1Name);
    await this.player2NameInput.fill(player2Name);
  }

  async selectGodCards(player1God: string, player2God: string) {
    await this.player1GodSelect.selectOption(player1God);
    await this.player2GodSelect.selectOption(player2God);
  }

  async startGame() {
    await this.startGameButton.click();
  }

  async verifyPageLoaded() {
    await expect(this.page).toHaveURL(/.*playerForm/);
    await expect(this.player1NameInput).toBeVisible();
    await expect(this.player2NameInput).toBeVisible();
    await expect(this.startGameButton).toBeVisible();
  }

  async verifyGodCardOptions(expectedGods: string[]) {
    for (const god of expectedGods) {
      await expect(this.player1GodSelect.locator(`option[value="${god}"]`)).toBeVisible();
    }
  }

  async verifyFormValidation() {
    // 测试空表单提交
    await this.startGameButton.click();
    await expect(this.page).toHaveURL(/.*playerForm/); // 应该停留在当前页面
  }
}

/**
 * 游戏板页面对象  
 */
export class GameBoardPage {
  readonly page: Page;
  readonly gameTitle: Locator;
  readonly gridButtons: Locator;
  readonly restartButton: Locator;
  readonly skipButton: Locator;
  readonly undoButton: Locator;
  readonly gameStatus: Locator;
  readonly winnerMessage: Locator;

  constructor(page: Page) {
    this.page = page;
    this.gameTitle = page.locator('h1:has-text("Game Started")');
    this.gridButtons = page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
    this.restartButton = page.locator('button:has-text("Restart Game")');
    this.skipButton = page.locator('button:has-text("Skip Current Action")');
    this.undoButton = page.locator('button:has-text("Undo")');
    this.gameStatus = page.locator('.text-danger, .text-success');
    this.winnerMessage = page.locator('h1:has-text("Winner")');
  }

  async verifyPageLoaded() {
    await expect(this.page).toHaveURL(/.*gameBoard/);
    await expect(this.gameTitle).toBeVisible();
  }

  async clickGridPosition(x: number, y: number) {
    const index = y * 5 + x; // 假设5x5网格
    await this.gridButtons.nth(index).click();
  }

  async verifyGridButtonsCount(expectedCount: number = 25) {
    const actualCount = await this.gridButtons.count();
    expect(actualCount).toBeGreaterThanOrEqual(expectedCount);
  }

  async verifyControlButtonsVisible() {
    await expect(this.restartButton).toBeVisible();
    await expect(this.skipButton).toBeVisible();
    await expect(this.undoButton).toBeVisible();
  }

  async verifyControlButtonsEnabled() {
    await expect(this.restartButton).toBeEnabled();
    await expect(this.skipButton).toBeEnabled();
    await expect(this.undoButton).toBeEnabled();
  }

  async clickRestartGame() {
    await this.restartButton.click();
  }

  async clickSkipAction() {
    await this.skipButton.click();
  }

  async clickUndo() {
    await this.undoButton.click();
  }

  async getGameStatus(): Promise<string | null> {
    try {
      return await this.gameStatus.textContent();
    } catch {
      return null;
    }
  }

  async verifyWinner(expectedWinner?: string) {
    await expect(this.winnerMessage).toBeVisible({ timeout: 2000 });
    if (expectedWinner) {
      const winnerText = await this.winnerMessage.textContent();
      expect(winnerText).toContain(expectedWinner);
    }
  }

  async verifyGameInProgress() {
    await expect(this.gameTitle).toBeVisible();
    // 确保没有显示胜利消息
    await expect(this.winnerMessage).not.toBeVisible();
  }

  /**
   * 等待游戏状态更新
   */
  async waitForStateUpdate(timeout: number = 1000) {
    await this.page.waitForTimeout(timeout);
  }

  /**
   * 获取网格按钮的状态信息
   */
  async getGridButtonState(x: number, y: number): Promise<any> {
    const index = y * 5 + x;
    const button = this.gridButtons.nth(index);
    
    return {
      isVisible: await button.isVisible(),
      isEnabled: await button.isEnabled(),
      text: await button.textContent(),
      classes: await button.getAttribute('class')
    };
  }
}

/**
 * 页面对象管理器
 */
export class PageObjectManager {
  readonly page: Page;
  readonly homePage: HomePage;
  readonly playerFormPage: PlayerFormPage;  
  readonly gameBoardPage: GameBoardPage;

  constructor(page: Page) {
    this.page = page;
    this.homePage = new HomePage(page);
    this.playerFormPage = new PlayerFormPage(page);
    this.gameBoardPage = new GameBoardPage(page);
  }

  /**
   * 快速设置完整游戏流程
   */
  async setupCompleteGame(player1Name: string, player2Name: string, 
                         player1God: string = 'Demeter', player2God: string = 'Pan') {
    await this.homePage.goto();
    await this.homePage.verifyPageLoaded();
    await this.homePage.startGame();
    
    await this.playerFormPage.verifyPageLoaded();
    await this.playerFormPage.fillPlayerInfo(player1Name, player2Name);
    await this.playerFormPage.selectGodCards(player1God, player2God);
    await this.playerFormPage.startGame();
    
    await this.gameBoardPage.verifyPageLoaded();
  }

  /**
   * 验证响应式设计
   */
  async verifyResponsiveDesign(viewportSize: { width: number; height: number }) {
    await this.page.setViewportSize(viewportSize);
    
    // 验证各页面在不同视口大小下的可见性
    await this.homePage.verifyPageLoaded();
  }
}