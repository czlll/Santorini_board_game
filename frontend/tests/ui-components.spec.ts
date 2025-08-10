import { test, expect } from '@playwright/test';

test.describe('UI Components and Interactions', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('Homepage UI elements', async ({ page }) => {
    // Verify main page elements are visible
    await expect(page.locator('button:has-text("Click Me to Start")')).toBeVisible();
    
    // Check if there are any images or game title
    const images = page.locator('img');
    const imageCount = await images.count();
    if (imageCount > 0) {
      await expect(images.first()).toBeVisible();
    }
    
    // Verify page title
    await expect(page).toHaveTitle(/.*Santorini.*/);
  });

  test('Player form UI validation', async ({ page }) => {
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Check all form elements are present
    const playerInputs = page.locator('input[placeholder="please enter your player name"]');
    await expect(playerInputs).toHaveCount(2);
    
    const godSelects = page.locator('select');
    await expect(godSelects).toHaveCount(2);
    
    const startButton = page.locator('button:has-text("Start Game")');
    await expect(startButton).toBeVisible();
    
    // Test placeholder text
    await expect(playerInputs.nth(0)).toHaveAttribute('placeholder', 'please enter your player name');
    await expect(playerInputs.nth(1)).toHaveAttribute('placeholder', 'please enter your player name');
  });

  test('God card dropdown options', async ({ page }) => {
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    const godSelect = page.locator('select').first();
    
    // Get all options
    const options = godSelect.locator('option');
    const optionCount = await options.count();
    expect(optionCount).toBeGreaterThan(1);
    
    // Check specific god cards are available
    const expectedGods = ['Apollo', 'Artemis', 'Athena', 'Atlas', 'Demeter', 'Hephaestus', 'Hermes', 'Minotaur', 'Pan', 'Prometheus'];
    
    for (const god of expectedGods) {
      await expect(godSelect.locator(`option[value="${god}"]`)).toBeVisible();
    }
  });

  test('Game board UI elements', async ({ page }) => {
    // Setup complete game
    await setupGame(page, 'Alice', 'Bob');
    
    // Check game board elements
    await expect(page.locator('h1:has-text("Game Started")')).toBeVisible();
    
    // Verify grid buttons are present (5x5 = 25 buttons + control buttons)
    const gridButtons = page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
    
    const buttonCount = await gridButtons.count();
    expect(buttonCount).toBeGreaterThanOrEqual(25);
    
    // Check control buttons
    await expect(page.locator('button:has-text("Restart Game")')).toBeVisible();
    await expect(page.locator('button:has-text("Skip Current Action")')).toBeVisible();
    await expect(page.locator('button:has-text("Undo")')).toBeVisible();
    
    // Check if there are status indicators
    const statusElements = page.locator('.text-danger, .text-success');
    // Status elements may or may not be visible initially
  });

  test('Button interactions and feedback', async ({ page }) => {
    await setupGame(page, 'Player1', 'Player2');
    
    const gridButtons = page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
    
    // Test clicking a grid button
    await gridButtons.nth(0).click();
    
    // Verify the click registered (button should remain clickable or change state)
    await expect(gridButtons.nth(0)).toBeVisible();
    
    // Test control buttons
    const restartButton = page.locator('button:has-text("Restart Game")');
    await expect(restartButton).toBeEnabled();
    
    const skipButton = page.locator('button:has-text("Skip Current Action")');
    await expect(skipButton).toBeEnabled();
    
    const undoButton = page.locator('button:has-text("Undo")');
    await expect(undoButton).toBeEnabled();
  });

  test('Responsive layout - tablet view', async ({ page }) => {
    // Set tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    
    // Test homepage
    await expect(page.locator('button:has-text("Click Me to Start")')).toBeVisible();
    
    // Test player form
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    const playerInputs = page.locator('input[placeholder="please enter your player name"]');
    await expect(playerInputs.nth(0)).toBeVisible();
    await expect(playerInputs.nth(1)).toBeVisible();
    
    const godSelects = page.locator('select');
    await expect(godSelects.nth(0)).toBeVisible();
    await expect(godSelects.nth(1)).toBeVisible();
    
    // Fill form and continue to game board
    await playerInputs.nth(0).fill('TabletPlayer1');
    await playerInputs.nth(1).fill('TabletPlayer2');
    await godSelects.nth(0).selectOption('Apollo');
    await godSelects.nth(1).selectOption('Pan');
    
    await page.click('button:has-text("Start Game")');
    await expect(page).toHaveURL(/.*gameBoard/);
    
    // Test game board on tablet
    await expect(page.locator('h1:has-text("Game Started")')).toBeVisible();
    
    const gridButtons = page.locator('button')
      .filter({ hasNotText: 'Restart Game' })
      .filter({ hasNotText: 'Skip Current Action' })
      .filter({ hasNotText: 'Undo' });
    
    // Grid buttons should still be clickable on tablet
    await expect(gridButtons.nth(0)).toBeVisible();
    await expect(gridButtons.nth(12)).toBeVisible(); // Middle button
    await expect(gridButtons.nth(24)).toBeVisible(); // Last button
  });

  test('CSS classes and styling', async ({ page }) => {
    await setupGame(page, 'Alice', 'Bob');
    
    // Check if CSS classes are applied correctly
    const gameTitle = page.locator('h1:has-text("Game Started")');
    
    // Check if status elements have proper classes
    const statusElements = page.locator('.text-danger, .text-success');
    if (await statusElements.count() > 0) {
      const firstStatus = statusElements.first();
      const className = await firstStatus.getAttribute('class');
      expect(className).toMatch(/text-(danger|success)/);
    }
  });

  test('Form validation feedback', async ({ page }) => {
    await page.click('button:has-text("Click Me to Start")');
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Try submitting form with empty fields
    await page.click('button:has-text("Start Game")');
    
    // Should stay on player form page
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Fill only one player name
    const playerInputs = page.locator('input[placeholder="please enter your player name"]');
    await playerInputs.nth(0).fill('OnlyOnePlayer');
    
    await page.click('button:has-text("Start Game")');
    
    // Should still stay on player form page
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Fill both player names
    await playerInputs.nth(1).fill('SecondPlayer');
    
    // Select god cards
    const godSelects = page.locator('select');
    await godSelects.nth(0).selectOption('Demeter');
    await godSelects.nth(1).selectOption('Pan');
    
    // Now should be able to proceed
    await page.click('button:has-text("Start Game")');
    await expect(page).toHaveURL(/.*gameBoard/);
  });

  test('Accessibility - keyboard navigation', async ({ page }) => {
    // Test tab navigation
    await page.keyboard.press('Tab');
    
    // The start button should be focusable
    const startButton = page.locator('button:has-text("Click Me to Start")');
    await expect(startButton).toBeFocused();
    
    // Test enter key
    await page.keyboard.press('Enter');
    await expect(page).toHaveURL(/.*playerForm/);
    
    // Test tab navigation on player form
    await page.keyboard.press('Tab');
    const firstInput = page.locator('input[placeholder="please enter your player name"]').first();
    await expect(firstInput).toBeFocused();
  });
});

// Helper function
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