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
        
        // Initialize locators
        this.startGameButton = page.locator("#start-game-btn");
        this.playerANameInput = page.locator("#player-a-name");
        this.playerBNameInput = page.locator("#player-b-name");
        this.gameBoard = page.locator("#game-board");
        this.currentPlayerDisplay = page.locator("#current-player");
        this.gameStatusDisplay = page.locator("#game-status");
        this.winnerMessage = page.locator("#winner-message");
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
        page.waitForSelector("#player-setup", new Page.WaitForSelectorOptions()
            .setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Set player names
     */
    public void setPlayerNames(String playerA, String playerB) {
        playerANameInput.fill(playerA);
        playerBNameInput.fill(playerB);
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
     * Place a worker at the specified position
     */
    public void placeWorker(int row, int col) {
        String cellSelector = String.format("#cell-%d-%d", row, col);
        Locator cell = page.locator(cellSelector);
        
        // Wait for cell to be clickable
        cell.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        cell.click();
        
        // Wait for worker placement animation
        page.waitForTimeout(300);
    }
    
    /**
     * Move a worker from one position to another
     */
    public void moveWorker(int fromRow, int fromCol, int toRow, int toCol) {
        // Click source position to select worker
        placeWorker(fromRow, fromCol);
        
        // Wait for valid move positions to be highlighted
        page.waitForSelector(".valid-move", new Page.WaitForSelectorOptions().setTimeout(2000));
        
        // Click target position
        placeWorker(toRow, toCol);
        
        // Wait for move completion
        page.waitForTimeout(500);
    }
    
    /**
     * Build a tower at the specified position
     */
    public void buildTower(int row, int col, boolean isDome) {
        String cellSelector = String.format("#cell-%d-%d", row, col);
        Locator cell = page.locator(cellSelector);
        
        if (isDome) {
            // Right-click to build dome
            cell.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        } else {
            // Left-click to build normal level
            cell.click();
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
        gameBoard.waitFor(new Locator.WaitForOptions().setTimeout(10000));
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