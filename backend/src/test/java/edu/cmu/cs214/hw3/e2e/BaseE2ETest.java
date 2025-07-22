package edu.cmu.cs214.hw3.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for E2E tests using Playwright
 * Provides common setup and teardown for browser automation
 */
public class BaseE2ETest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    protected static final String BASE_URL = "http://localhost:8080";
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        
        // Configure browser launch options
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "true")))
            .setSlowMo(Integer.parseInt(System.getProperty("slowMo", "50")));
        
        browser = playwright.chromium().launch(launchOptions);
    }
    
    @AfterAll
    static void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @BeforeEach
    void createContextAndPage() {
        // Create new browser context (isolated session)
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720)
            .setLocale("en-US"));
        
        page = context.newPage();
        
        // Start application server if needed
        startApplicationServer();
    }
    
    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
        
        // Stop application server if needed
        stopApplicationServer();
    }
    
    /**
     * Override this method to start your application server
     */
    protected void startApplicationServer() {
        // Implementation depends on how you want to start your NanoHTTPD server
        // For now, assume the server is already running
    }
    
    /**
     * Override this method to stop your application server
     */
    protected void stopApplicationServer() {
        // Implementation for stopping the server
    }
    
    /**
     * Wait for the game board to load
     */
    protected void waitForGameToLoad() {
        page.waitForSelector("#game-board", new Page.WaitForSelectorOptions().setTimeout(5000));
    }
    
    /**
     * Wait for a specific player's turn
     */
    protected void waitForPlayerTurn(String playerName) {
        page.waitForSelector(String.format("text=%s's turn", playerName));
    }
    
    /**
     * Take a screenshot for debugging purposes
     */
    protected void takeScreenshot(String name) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("target/screenshots"));
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get(String.format("target/screenshots/%s.png", name))));
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}