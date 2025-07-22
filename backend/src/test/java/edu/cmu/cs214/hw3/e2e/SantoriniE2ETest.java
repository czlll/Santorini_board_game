package edu.cmu.cs214.hw3.e2e;

import edu.cmu.cs214.hw3.e2e.pages.SantoriniGamePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End tests for Santorini Board Game
 * Tests complete user workflows from UI perspective
 */
public class SantoriniE2ETest extends BaseE2ETest {
    
    @Test
    @DisplayName("Complete Game Flow - From Start to Victory")
    void testCompleteGameFlow() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // 1. Navigate to game page
        gamePage.navigateToGame();
        
        // 2. Start new game
        gamePage.startNewGame();
        
        // 3. Set player names
        gamePage.setPlayerNames("Alice", "Bob");
        
        // 4. Player A selects Apollo god card
        gamePage.selectGodCard("Apollo");
        
        // 5. Player B selects Demeter god card
        gamePage.selectGodCard("Demeter");
        
        // 6. Verify game enters worker placement phase
        assertTrue(gamePage.getGameStatus().contains("Place"));
        
        // 7. Place workers
        gamePage.placeWorker(0, 0); // Alice worker 1
        gamePage.placeWorker(1, 1); // Alice worker 2
        gamePage.placeWorker(2, 2); // Bob worker 1
        gamePage.placeWorker(3, 3); // Bob worker 2
        
        // 8. Verify workers are placed successfully
        assertTrue(gamePage.hasWorkerAt(0, 0, "Alice"));
        assertTrue(gamePage.hasWorkerAt(1, 1, "Alice"));
        assertTrue(gamePage.hasWorkerAt(2, 2, "Bob"));
        assertTrue(gamePage.hasWorkerAt(3, 3, "Bob"));
        
        // 9. Verify game enters move phase
        gamePage.waitForGamePhase("Move");
        assertTrue(gamePage.getGameStatus().contains("Move"));
        
        // 10. Execute first round of move and build
        gamePage.moveWorker(0, 0, 0, 1);
        gamePage.buildTower(0, 2, false);
        
        // 11. Verify tower is built successfully
        assertEquals(1, gamePage.getTowerLevel(0, 2));
        
        // Take screenshot to save current state
        gamePage.takeScreenshot("game-in-progress");
    }
    
    @Test
    @DisplayName("Apollo God Card Special Ability - Worker Position Swap")
    void testApolloSpecialAbility() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // Set up test scenario
        setupApolloTestScenario(gamePage);
        
        // Verify initial positions
        assertTrue(gamePage.hasWorkerAt(1, 1, "Alice"));
        assertTrue(gamePage.hasWorkerAt(1, 2, "Bob"));
        
        // Execute Apollo's swap move
        gamePage.moveWorker(1, 1, 1, 2);
        
        // Verify position swap is successful
        assertTrue(gamePage.hasWorkerAt(1, 2, "Alice")); // Apollo worker moved here
        assertTrue(gamePage.hasWorkerAt(1, 1, "Bob"));   // Opponent worker swapped here
        
        gamePage.takeScreenshot("apollo-swap-complete");
    }
    
    @Test
    @DisplayName("Demeter God Card Special Ability - Double Build")
    void testDemeterDoubleBuild() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // Set up test scenario
        setupDemeterTestScenario(gamePage);
        
        // Move Demeter worker
        gamePage.moveWorker(2, 2, 2, 3);
        
        // First build
        gamePage.buildTower(2, 4, false);
        assertEquals(1, gamePage.getTowerLevel(2, 4));
        
        // Verify second build option is available
        assertTrue(gamePage.getGameStatus().contains("second build") || 
                  gamePage.getGameStatus().contains("optional"));
        
        // Second build
        gamePage.buildTower(1, 4, false);
        assertEquals(1, gamePage.getTowerLevel(1, 4));
        
        gamePage.takeScreenshot("demeter-double-build");
    }
    
    @Test
    @DisplayName("Win Condition Test - Reaching Level 3 Tower")
    void testWinCondition() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // Set up near-win scenario
        setupNearWinScenario(gamePage);
        
        // Execute winning move
        gamePage.moveWorker(1, 1, 1, 2); // Move to level 3 tower
        
        // Verify game ends
        assertTrue(gamePage.isGameFinished());
        assertTrue(gamePage.getWinnerMessage().contains("Alice"));
        
        gamePage.takeScreenshot("victory-screen");
    }
    
    @Test
    @DisplayName("Error Handling Test - Invalid Move")
    void testInvalidMoveHandling() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        setupBasicGameScenario(gamePage);
        
        // Attempt invalid move (move to position with existing worker)
        gamePage.moveWorker(0, 0, 1, 1); // 1,1 already has worker
        
        // Verify error message is displayed
        assertTrue(gamePage.getGameStatus().contains("Invalid") || 
                  gamePage.getGameStatus().contains("cannot"));
        
        // Verify worker positions haven't changed
        assertTrue(gamePage.hasWorkerAt(0, 0, "Alice"));
        assertTrue(gamePage.hasWorkerAt(1, 1, "Alice"));
    }
    
    @Test
    @DisplayName("Building Rules Test - Dome Construction")
    void testDomeBuilding() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        setupTowerBuildingScenario(gamePage);
        
        // Build to level 3
        gamePage.moveWorker(1, 1, 1, 0);
        gamePage.buildTower(0, 0, false); // Build level 1
        
        // Continue building to level 3 (requires multiple rounds)
        for (int i = 2; i <= 3; i++) {
            // Simulate multiple game rounds to build to level 3
            simulateGameRounds(gamePage, i);
        }
        
        // Build dome
        gamePage.moveWorker(1, 0, 1, 1);
        gamePage.buildTower(0, 0, true); // Build dome
        
        // Verify dome is built successfully
        assertTrue(gamePage.hasDome(0, 0));
        assertEquals(4, gamePage.getTowerLevel(0, 0)); // 3 levels + dome = 4
    }
    
    @Test
    @DisplayName("Game State Persistence Test")
    void testGameStatePersistence() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // Start game and perform some operations
        setupBasicGameScenario(gamePage);
        gamePage.moveWorker(0, 0, 0, 1);
        gamePage.buildTower(0, 2, false);
        
        // Refresh page
        page.reload();
        gamePage.waitForGameToLoad();
        
        // Verify game state is maintained
        assertTrue(gamePage.hasWorkerAt(0, 1, "Alice"));
        assertEquals(1, gamePage.getTowerLevel(0, 2));
    }
    
    @Test
    @DisplayName("Responsive Design Test - Different Screen Sizes")
    void testResponsiveDesign() {
        SantoriniGamePage gamePage = new SantoriniGamePage(page);
        
        // Test desktop size
        page.setViewportSize(1920, 1080);
        gamePage.navigateToGame();
        assertTrue(gamePage.getGameBoard().isVisible());
        
        // Test tablet size
        page.setViewportSize(768, 1024);
        page.reload();
        assertTrue(gamePage.getGameBoard().isVisible());
        
        // Test mobile size
        page.setViewportSize(375, 667);
        page.reload();
        assertTrue(gamePage.getGameBoard().isVisible());
        
        gamePage.takeScreenshot("mobile-view");
    }
    
    // Helper methods for setting up test scenarios
    private void setupApolloTestScenario(SantoriniGamePage gamePage) {
        // Set up specific game state via API or direct manipulation
        page.navigate(BASE_URL + "/test-setup?scenario=apollo");
        gamePage.waitForGameToLoad();
    }
    
    private void setupDemeterTestScenario(SantoriniGamePage gamePage) {
        page.navigate(BASE_URL + "/test-setup?scenario=demeter");
        gamePage.waitForGameToLoad();
    }
    
    private void setupNearWinScenario(SantoriniGamePage gamePage) {
        page.navigate(BASE_URL + "/test-setup?scenario=near-win");
        gamePage.waitForGameToLoad();
    }
    
    private void setupBasicGameScenario(SantoriniGamePage gamePage) {
        page.navigate(BASE_URL + "/test-setup?scenario=basic");
        gamePage.waitForGameToLoad();
    }
    
    private void setupTowerBuildingScenario(SantoriniGamePage gamePage) {
        page.navigate(BASE_URL + "/test-setup?scenario=tower-building");
        gamePage.waitForGameToLoad();
    }
    
    private void simulateGameRounds(SantoriniGamePage gamePage, int targetLevel) {
        // Simulate multiple game rounds to build to specified level
        // Implementation depends on actual game logic
    }
}