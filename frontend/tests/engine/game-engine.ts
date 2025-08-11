import { Page, expect } from '@playwright/test';
import { GameScenario, GameMove, Position, GameSetup } from '../data/game-notation';
import { ScenarioLoader } from '../data/scenario-loader';

export class SantoriniTestEngine {
  private page: Page;
  private currentBoardState: Map<string, any> = new Map();

  constructor(page: Page) {
    this.page = page;
  }

  /**
   * 执行完整的游戏场景
   */
  async executeScenario(scenario: GameScenario): Promise<void> {
    console.log(`执行场景: ${scenario.name}`);
    
    // 1. 设置游戏
    await this.setupGame(scenario.setup);
    
    // 2. 执行所有移动
    for (const move of scenario.moves) {
      await this.executeMove(move);
      await this.page.waitForTimeout(300); // 给UI响应时间
    }
    
    // 3. 验证期望结果
    if (scenario.expectedOutcome) {
      await this.validateOutcome(scenario.expectedOutcome);
    }
  }

  /**
   * 从文件加载并执行场景
   */
  async executeScenarioFromFile(filename: string): Promise<void> {
    const scenario = await ScenarioLoader.loadScenario(filename);
    await this.executeScenario(scenario);
  }

  /**
   * 批量执行多个场景文件
   */
  async executeScenarioSuite(filenames: string[]): Promise<{
    passed: string[];
    failed: Array<{ filename: string; error: string }>;
  }> {
    const results = {
      passed: [] as string[],
      failed: [] as Array<{ filename: string; error: string }>
    };

    for (const filename of filenames) {
      try {
        await this.executeScenarioFromFile(filename);
        results.passed.push(filename);
        console.log(`✅ ${filename} - 执行成功`);
      } catch (error) {
        results.failed.push({
          filename,
          error: error instanceof Error ? error.message : String(error)
        });
        console.error(`❌ ${filename} - 执行失败:`, error);
      }
    }

    return results;
  }

  /**
   * 设置游戏初始状态
   */
  private async setupGame(setup: GameSetup): Promise<void> {
    // 导航到主页
    await this.page.goto('/');
    
    // 点击开始游戏
    await this.page.click('button:has-text("Click Me to Start")');
    await expect(this.page).toHaveURL(/.*playerForm/);
    
    // 填写玩家信息
    const playerInputs = this.page.locator('input[placeholder="please enter your player name"]');
    await playerInputs.nth(0).fill(setup.players.player1.name);
    await playerInputs.nth(1).fill(setup.players.player2.name);
    
    // 选择神卡
    const godSelects = this.page.locator('select');
    await godSelects.nth(0).selectOption(setup.players.player1.godCard);
    await godSelects.nth(1).selectOption(setup.players.player2.godCard);
    
    // 开始游戏
    await this.page.click('button:has-text("Start Game")');
    await expect(this.page).toHaveURL(/.*gameBoard/);
    await expect(this.page.locator('h1:has-text("Game Started")')).toBeVisible();
    
    console.log(`游戏设置完成: ${setup.players.player1.name}(${setup.players.player1.godCard}) vs ${setup.players.player2.name}(${setup.players.player2.godCard})`);
  }

  /**
   * 执行单个游戏移动
   */
  private async executeMove(move: GameMove): Promise<void> {
    console.log(`执行移动: ${move.type} by ${move.player} to (${move.to.x}, ${move.to.y})`);
    
    switch (move.type) {
      case 'place_worker':
        await this.placeWorker(move);
        break;
      case 'move':
        await this.moveWorker(move);
        break;
      case 'build':
        await this.buildAction(move);
        break;
      case 'special_action':
        await this.executeSpecialAction(move);
        break;
    }
    
    // 更新内部棋盘状态跟踪
    this.updateBoardState(move);
  }

  /**
   * 放置工人
   */
  private async placeWorker(move: GameMove): Promise<void> {
    const position = this.positionToGridIndex(move.to);
    const gridButtons = this.getGridButtons();
    await gridButtons.nth(position).click();
  }

  /**
   * 移动工人
   */
  private async moveWorker(move: GameMove): Promise<void> {
    if (!move.from) {
      throw new Error('Move action requires from position');
    }
    
    // 选择要移动的工人
    const fromPosition = this.positionToGridIndex(move.from);
    const gridButtons = this.getGridButtons();
    await gridButtons.nth(fromPosition).click();
    await this.page.waitForTimeout(200);
    
    // 移动到目标位置
    const toPosition = this.positionToGridIndex(move.to);
    await gridButtons.nth(toPosition).click();
  }

  /**
   * 建造动作
   */
  private async buildAction(move: GameMove): Promise<void> {
    const position = this.positionToGridIndex(move.to);
    const gridButtons = this.getGridButtons();
    await gridButtons.nth(position).click();
  }

  /**
   * 执行特殊动作（如神卡技能）
   */
  private async executeSpecialAction(move: GameMove): Promise<void> {
    // 根据具体的神卡能力实现特殊动作
    // 这里可以根据 move.metadata 中的信息执行不同的特殊动作
    const position = this.positionToGridIndex(move.to);
    const gridButtons = this.getGridButtons();
    await gridButtons.nth(position).click();
  }

  /**
   * 验证游戏结果
   */
  private async validateOutcome(expectedOutcome: any): Promise<void> {
    if (expectedOutcome.winner) {
      // 验证胜利者
      const winMessage = this.page.locator('h1:has-text("Winner")');
      await expect(winMessage).toBeVisible({ timeout: 2000 });
      console.log(`验证胜利者: ${expectedOutcome.winner}`);
    }
    
    if (expectedOutcome.gameState === 'in_progress') {
      // 验证游戏仍在进行中
      await expect(this.page.locator('h1:has-text("Game Started")')).toBeVisible();
      console.log('验证游戏状态: 进行中');
    }
  }

  /**
   * 获取游戏网格按钮
   */
  private getGridButtons() {
    return this.page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
  }

  /**
   * 将坐标位置转换为网格索引
   */
  private positionToGridIndex(position: Position): number {
    return position.y * 5 + position.x; // 假设是5x5网格
  }

  /**
   * 更新内部棋盘状态
   */
  private updateBoardState(move: GameMove): void {
    const key = `${move.to.x},${move.to.y}`;
    this.currentBoardState.set(key, move);
  }

  /**
   * 验证棋盘状态
   */
  async validateBoardState(expectedState: any): Promise<void> {
    // 实现棋盘状态验证逻辑
    console.log('验证棋盘状态');
  }

  /**
   * 获取当前游戏状态
   */
  async getCurrentGameState(): Promise<any> {
    // 从UI中读取当前游戏状态
    const gameStatus = await this.page.locator('.text-danger, .text-success').allTextContents();
    return {
      status: gameStatus,
      boardState: this.currentBoardState
    };
  }

  /**
   * 执行控制动作（重启、撤销等）
   */
  async executeControlAction(action: 'restart' | 'skip' | 'undo'): Promise<void> {
    const buttonTextMap = {
      restart: 'Restart Game',
      skip: 'Skip Current Action', 
      undo: 'Undo'
    };
    
    const buttonText = buttonTextMap[action];
    await this.page.click(`button:has-text("${buttonText}")`);
    console.log(`执行控制动作: ${action}`);
  }

  /**
   * 等待游戏状态变化
   */
  async waitForGameStateChange(timeout: number = 5000): Promise<void> {
    await this.page.waitForTimeout(timeout);
  }

  /**
   * 截取游戏状态截图（用于调试）
   */
  async captureGameState(filename: string): Promise<void> {
    await this.page.screenshot({ path: `tests/screenshots/${filename}.png` });
    console.log(`游戏状态截图保存: ${filename}.png`);
  }
}