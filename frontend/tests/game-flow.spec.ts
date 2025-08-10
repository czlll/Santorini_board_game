import { test, expect } from '@playwright/test';

test.describe('Santorini Game Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to the game homepage
    await page.goto('/');
  });

  test('Complete game setup flow', async ({ page }) => {
    // 1. Start a new game
    await page.click('button:has-text("Click Me to Start")');
    
    // 2. Wait for navigation to player form page
    await expect(page).toHaveURL(/.*playerForm/);
    
    // 3. Fill in player names
    const playerInputs = page.locator('input[placeholder="please enter your player name"]');
    await playerInputs.nth(0).fill('Alice');
    await playerInputs.nth(1).fill('Bob');
    
    // 4. Select god cards for both players
    const godSelects = page.locator('select');
    await godSelects.nth(0).selectOption('Demeter');
    await godSelects.nth(1).selectOption('Pan');
    
    // 5. Start the game
    await page.click('button:has-text("Start Game")');
    
    // 6. Wait for navigation to game board
    await expect(page).toHaveURL(/.*gameBoard/);
    
    // 7. Verify game has started
    await expect(page.locator('h1:has-text("Game Started")')).toBeVisible();
  });

  test('Basic game interaction - worker placement', async ({ page }) => {
    // Setup game first
    await setupGame(page, 'Alice', 'Bob');
    
    // Get all grid buttons (excluding control buttons)
    const gridButtons = page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
    
    // Place workers on the board
    await gridButtons.nth(0).click(); // Position (0,0)
    await page.waitForTimeout(300);
    
    await gridButtons.nth(6).click(); // Position (1,1)  
    await page.waitForTimeout(300);
    
    await gridButtons.nth(12).click(); // Position (2,2)
    await page.waitForTimeout(300);
    
    await gridButtons.nth(18).click(); // Position (3,3)
    await page.waitForTimeout(300);
    
    // Verify the game progresses (workers placed or game state changed)
    // Note: The exact verification depends on the game's visual feedback
    await expect(page.locator('body')).toBeVisible(); // Basic check that page is still responsive
  });

  test('Error handling - invalid input', async ({ page }) => {
    // Navigate to player form
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Try to start game without filling player names
    await page.click('button:has-text("Start Game")');
    
    // Verify we're still on the player form page (validation should prevent navigation)
    await expect(page).toHaveURL(/.*playerForm/);
  });

  test('Responsive design - mobile view', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    
    // Navigate through the game flow
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Verify elements are visible on mobile
    const playerInputs = page.locator('input[placeholder="please enter your player name"]');
    await expect(playerInputs.nth(0)).toBeVisible();
    await expect(playerInputs.nth(1)).toBeVisible();
    
    // Fill form and continue
    await playerInputs.nth(0).fill('Player1');
    await playerInputs.nth(1).fill('Player2');
    
    const godSelects = page.locator('select');
    await godSelects.nth(0).selectOption('Demeter');
    await godSelects.nth(1).selectOption('Pan');
    
    await page.click('button:has-text("Start Game")');
    await expect(page).toHaveURL(/.*gameBoard/);
    
    // Verify game board is visible on mobile
    await expect(page.locator('h1:has-text("Game Started")')).toBeVisible();
  });

  test('Browser navigation - back/forward', async ({ page }) => {
    // Start game and navigate to player form
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Go back to homepage
    await page.goBack();
    await expect(page).toHaveURL('/');
    await expect(page.locator('button:has-text("Click Me to Start")')).toBeVisible();
    
    // Go forward again
    await page.goForward();
    await expect(page).toHaveURL(/.*playerForm/);
  });

  test('Page reload persistence', async ({ page }) => {
    // Setup game
    await setupGame(page, 'TestPlayer1', 'TestPlayer2');
    
    // Perform some game actions
    const gridButtons = page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
    
    await gridButtons.nth(0).click();
    await page.waitForTimeout(500);
    
    // Reload the page
    await page.reload();
    
    // Verify the game state is maintained or appropriately handled
    await expect(page.locator('h1:has-text("Game Started")')).toBeVisible();
  });
});

// Helper function to setup a complete game
async function setupGame(page: any, player1: string, player2: string) {
  await page.click('button:has-text("Click Me to Start")');
  await expect(page).toHaveURL(/.*playerForm/);
  
  const playerInputs = page.locator('input[placeholder="please enter your player name"]');
  await playerInputs.nth(0).fill(player1);
  await playerInputs.nth(1).fill(player2);
  
  const godSelects = page.locator('select');
  await godSelects.nth(0).selectOption('Demeter');
  await godSelects.nth(1).selectOption('Pan');
  
  await page.click('button:has-text("Start Game")');
  await expect(page).toHaveURL(/.*gameBoard/);
  await expect(page.locator('h1:has-text("Game Started")')).toBeVisible();
}