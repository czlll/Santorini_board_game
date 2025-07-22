package edu.cmu.cs214.hw3.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.MouseButton;

/**
 * Page Object Model for Santorini Game Page
 * Encapsulates UI elements and operations for the game interface
 */
public class SantoriniGamePage {
    private final Page page;
    
    // Page element locators
    private final Locator startGameButton;
    private final Locator playerANameInput;
    private final Locator playerBNameInput;
    private final Locator gameBoard;
    private final Locator currentPlayerDisplay;
    private final Locator gameStatusDisplay;
    private final Locator winnerMessage;
    
    public SantoriniGamePage(Page page) {
        this.page = page;
        
        // Initialize locators based on actual frontend structure
        this.startGameButton = page.locator("button:has-text('Click Me to Start')");
        this.playerANameInput = page.locator("input[placeholder='please enter your player name']").first();
        this.playerBNameInput = page.locator("input[placeholder='please enter your player name']").nth(1);
        this.gameBoard = page.locator("h1:has-text('Game Started')"); // Use game started header as indicator
        this.currentPlayerDisplay = page.locator("h1:has-text('Game Started')");
        this.gameStatusDisplay = page.locator(".text-danger, .text-success");
        this.winnerMessage = page.locator("h1:has-text('Winner')");
    }
    
    /**
     * Navigate to the game page
     */
    public void navigateToGame() {
        page.navigate("http://localhost:8080");
        page.waitForLoadState();
    }
    
    /**
     * Start a new game
     */
    public void startNewGame() {
        startGameButton.click();
        // Wait for navigation to player form (note: URL is case sensitive)
        page.waitForURL("**/playerForm", new Page.WaitForURLOptions().setTimeout(5000));
    }
    
    /**
     * Set player names
     */
    public void setPlayerNames(String playerA, String playerB) {
        playerANameInput.fill(playerA);
        playerBNameInput.fill(playerB);
    }
    
    /**
     * Complete game setup with player names and god cards
     */
    public void setupGame(String playerA, String playerB) {
        // Set player names
        setPlayerNames(playerA, playerB);
        
        // Select different god cards for each player
        Locator player1GodSelect = page.locator("select").first();
        Locator player2GodSelect = page.locator("select").nth(1);
        
        player1GodSelect.selectOption("Demeter");
        player2GodSelect.selectOption("Pan");
        
        // Click start game button
        page.locator("button:has-text('Start Game')").click();
        
        // Wait for navigation to game board (note: URL is case sensitive)
        page.waitForURL("**/gameBoard", new Page.WaitForURLOptions().setTimeout(10000));
    }
    
    /**
     * Select a god card
     */
    public void selectGodCard(String godCardName) {
        Locator godCard = page.locator(String.format("#%s-card", godCardName.toLowerCase()));
        godCard.click();
        
        // Wait for selection confirmation
        page.waitForTimeout(500);
    }
    
    /**
     * Get the button ID for a given row and column
     * Based on the actual button IDs observed in the game
     */
    private int getButtonId(int row, int col) {
        // Button IDs follow this pattern:
        // Row 0: 60, 61, 62, 63, 64
        // Row 1: 67, 68, 69, 70, 71  
        // Row 2: 74, 75, 76, 77, 78
        // Row 3: 81, 82, 83, 84, 85
        // Row 4: 88, 89, 90, 91, 92
        int baseId = 60 + (row * 7) + col;
        return baseId;
    }
    
    /**
     * Place a worker at the specified position
     */
    public void placeWorker(int row, int col) {
        // Get all grid buttons and select by index
        // The grid has 25 buttons total (5x5)
        int buttonIndex = row * 5 + col;
        
        // Find all buttons in the game grid area
        Locator allButtons = page.locator("button").filter(new Locator.FilterOptions().setHasNotText("Restart Game"))
                                                   .filter(new Locator.FilterOptions().setHasNotText("Skip Current Action"))
                                                   .filter(new Locator.FilterOptions().setHasNotText("Undo"));
        
        // Select the specific button by index
        Locator gridButton = allButtons.nth(buttonIndex);
        
        // Wait for cell to be clickable
        gridButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        gridButton.click();
        
        // Wait for worker placement animation
        page.waitForTimeout(300);
    }
    
    /**
     * Move a worker from one position to another
     */
    public void moveWorker(int fromRow, int fromCol, int toRow, int toCol) {
        // Click source position to select worker
        placeWorker(fromRow, fromCol);
        
        // Wait a moment for the game to process the selection
        page.waitForTimeout(500);
        
        // Click target position
        placeWorker(toRow, toCol);
        
        // Wait for move completion
        page.waitForTimeout(500);
    }
    
    /**
     * Build a tower at the specified position
     */
    public void buildTower(int row, int col, boolean isDome) {
        // Get all grid buttons and select by index
        int buttonIndex = row * 5 + col;
        
        // Find all buttons in the game grid area
        Locator allButtons = page.locator("button").filter(new Locator.FilterOptions().setHasNotText("Restart Game"))
                                                   .filter(new Locator.FilterOptions().setHasNotText("Skip Current Action"))
                                                   .filter(new Locator.FilterOptions().setHasNotText("Undo"));
        
        // Select the specific button by index
        Locator gridButton = allButtons.nth(buttonIndex);
        
        if (isDome) {
            // Right-click to build dome
            gridButton.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        } else {
            // Left-click to build normal level
            gridButton.click();
        }
        
        // Wait for building completion
        page.waitForTimeout(300);
    }
    
    /**
     * Get the current player name
     */
    public String getCurrentPlayer() {
        return currentPlayerDisplay.textContent();
    }
    
    /**
     * Get the current game status
     */
    public String getGameStatus() {
        return gameStatusDisplay.textContent();
    }
    
    /**
     * Get the winner message
     */
    public String getWinnerMessage() {
        winnerMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        return winnerMessage.textContent();
    }
    
    /**
     * Check if the game is finished
     */
    public boolean isGameFinished() {
        return winnerMessage.isVisible();
    }
    
    /**
     * Get the tower level at the specified position
     */
    public int getTowerLevel(int row, int col) {
        String cellSelector = String.format("#cell-%d-%d .tower-level", row, col);
        Locator towerLevel = page.locator(cellSelector);
        
        if (towerLevel.isVisible()) {
            return Integer.parseInt(towerLevel.textContent());
        }
        return 0;
    }
    
    /**
     * Check if there's a dome at the specified position
     */
    public boolean hasDome(int row, int col) {
        String domeSelector = String.format("#cell-%d-%d .dome", row, col);
        return page.locator(domeSelector).isVisible();
    }
    
    /**
     * Check if there's a worker at the specified position for the given player
     */
    public boolean hasWorkerAt(int row, int col, String playerName) {
        String workerSelector = String.format("#cell-%d-%d .worker.player-%s", 
            row, col, playerName.toLowerCase());
        return page.locator(workerSelector).isVisible();
    }
    
    /**
     * Wait for a specific player's turn
     */
    public void waitForPlayerTurn(String playerName) {
        page.waitForSelector(String.format("text=*%s*", playerName), 
            new Page.WaitForSelectorOptions().setTimeout(5000));
    }
    
    /**
     * Wait for a specific game phase
     */
    public void waitForGamePhase(String phase) {
        page.waitForSelector(String.format("text=*%s*", phase),
            new Page.WaitForSelectorOptions().setTimeout(5000));
    }
    
    /**
     * Get the game board locator for direct access in tests
     */
    public Locator getGameBoard() {
        return gameBoard;
    }
    
    /**
     * Wait for the game to load completely
     */
    public void waitForGameToLoad() {
        // Wait for either the game started header or the game grid to be visible
        try {
            gameBoard.waitFor(new Locator.WaitForOptions().setTimeout(15000));
        } catch (Exception e) {
            // Fallback: wait for game grid buttons to be visible
            page.locator("button").filter(new Locator.FilterOptions().setHasNotText("Restart Game"))
                                  .filter(new Locator.FilterOptions().setHasNotText("Skip Current Action"))
                                  .filter(new Locator.FilterOptions().setHasNotText("Undo"))
                                  .first()
                                  .waitFor(new Locator.WaitForOptions().setTimeout(10000));
        }
    }
    
    /**
     * Take a screenshot for debugging
     */
    public void takeScreenshot(String name) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("target/screenshots"));
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get(String.format("target/screenshots/%s.png", name))));
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}