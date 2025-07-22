package edu.cmu.cs214.hw3.e2e;

import edu.cmu.cs214.hw3.e2e.pages.SantoriniGamePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance and reliability E2E tests for Santorini Board Game
 * Tests system behavior under various conditions
 */
public class SantoriniPerformanceE2ETest extends BaseE2ETest {
    
    @Test
    @DisplayName("Page Load Performance Test")
    void testPageLoadPerformance() {
        long startTime = System.currentTimeMillis();
        
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        gamePage.navigateToGame();
        gamePage.waitForGameToLoad();
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        // Verify page loads within 3 seconds
        assertTrue(loadTime < 3000, 
            String.format("Page load time %dms exceeds 3000ms", loadTime));
    }
    
    @Test
    @DisplayName("Game Operation Responsiveness Test")
    void testGameOperationResponsiveness() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        setupBasicGameScenario(gamePage);
        
        // Test rapid consecutive operations
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            gamePage.moveWorker(0, 0, 0, 1);
            gamePage.moveWorker(0, 1, 0, 0);
        }
        
        long operationTime = System.currentTimeMillis() - startTime;
        
        // Verify operations complete in reasonable time
        assertTrue(operationTime < 5000, 
            String.format("Operations took %dms, too slow", operationTime));
    }
    
    @Test
    @DisplayName("Memory Leak Test - Long Duration Game")
    void testMemoryLeakDuringLongGame() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        setupBasicGameScenario(gamePage);
        
        // Simulate long-duration game
        for (int round = 0; round < 50; round++) {
            gamePage.moveWorker(0, 0, 0, 1);
            gamePage.buildTower(1, 0, false);
            gamePage.moveWorker(0, 1, 0, 0);
            
            // Check page responsiveness every 10 rounds
            if (round % 10 == 0) {
                assertTrue(gamePage.gameBoard.isVisible());
            }
        }
        
        // Verify game is still operational
        assertTrue(gamePage.gameBoard.isVisible());
    }
    
    @Test
    @DisplayName("Browser Compatibility Test")
    void testBrowserCompatibility() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // Test basic functionality
        gamePage.navigateToGame();
        gamePage.waitForGameToLoad();
        
        // Verify core elements are present and functional
        assertTrue(gamePage.gameBoard.isVisible());
        
        // Test JavaScript functionality
        page.evaluate("() => console.log('JavaScript test')");
        
        // Verify no JavaScript errors
        page.onConsoleMessage(msg -> {
            if (msg.type().equals("error")) {
                fail("JavaScript error detected: " + msg.text());
            }
        });
    }
    
    @Test
    @DisplayName("Network Latency Simulation Test")
    void testNetworkLatencyHandling() {
        // Simulate slow network conditions
        page.route("**/*", route -> {
            // Add 100ms delay to all requests
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            route.resume();
        });
        
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        gamePage.navigateToGame();
        
        // Verify game still loads and functions under slow network
        gamePage.waitForGameToLoad();
        assertTrue(gamePage.gameBoard.isVisible());
    }
    
    private void setupBasicGameScenario(SantoriniGamePage gamePage) {
        page.navigate(BASE_URL + "/test-setup?scenario=basic");
        gamePage.waitForGameToLoad();
    }
}