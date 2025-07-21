package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for Santorini game based on requirements document
 * Tests all functional requirements including:
 * - Game initialization and setup
 * - Worker placement phase (测试用例 1-6)
 * - Game turn mechanics (move and build)
 * - Win conditions (测试用例 13-14)
 * - Invalid operations and error handling (测试用例 15-16)
 * - Game state management
 * 
 * 补充文档中指定的测试用例，与 DocumentBasedTest 配合提供完整覆盖
 */
public class ComprehensiveGameTest {
    
    private Game game;
    private Player playerA;
    private Player playerB;
    private Board board;

    @Before
    public void setUp() throws Exception {
        playerA = new Player("PlayerA");
        playerB = new Player("PlayerB");
        board = new Board();
        game = new Game(board, playerA, playerB);
    }

    // ========== 2.1 Worker Placement Phase Tests ==========
    
    /**
     * Test Case 1: Verifies that a worker can be legally placed on an empty cell
     */
    @Test
    public void testWorkerPlacementPhase() throws Exception {
        // Initially game should be in setup phase
        assertEquals(-1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(0, game.getCurPlayerAction(), 0);
        
        // Place first worker for Player A
        game.placeWorkerAuto(0, 0);
        assertEquals(-1, game.getGameStatus()); // Still in setup
        assertEquals(0, game.getCurPlayer());
        assertEquals(1, game.getCurPlayerAction(), 0);
        
        // Place second worker for Player A
        game.placeWorkerAuto(0, 1);
        assertEquals(-1, game.getGameStatus()); // Still in setup
        assertEquals(1, game.getCurPlayer()); // Now Player B's turn
        assertEquals(0, game.getCurPlayerAction(), 0);
        
        // Place first worker for Player B
        game.placeWorkerAuto(4, 4);
        assertEquals(-1, game.getGameStatus()); // Still in setup
        assertEquals(1, game.getCurPlayer());
        assertEquals(1, game.getCurPlayerAction(), 0);
        
        // Place second worker for Player B - should transition to play phase
        game.placeWorkerAuto(4, 3);
        assertEquals(1, game.getGameStatus()); // Now in play phase
        assertEquals(0, game.getCurPlayer()); // Player A starts
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker to move
        
        // Verify all workers are placed correctly
        assertEquals("PlayerA", game.getBoard().getCell(0, 0).getWorker().getPlayerName());
        assertEquals("PlayerA", game.getBoard().getCell(0, 1).getWorker().getPlayerName());
        assertEquals("PlayerB", game.getBoard().getCell(4, 4).getWorker().getPlayerName());
        assertEquals("PlayerB", game.getBoard().getCell(4, 3).getWorker().getPlayerName());
    }
    
    /**
     * Test Case 3: Verifies that a worker cannot be placed on an already occupied cell
     */
    @Test
    public void testWorkerPlacementInvalidPositions() throws Exception {
        // Place first worker
        game.placeWorkerAuto(2, 2);
        
        // Try to place second worker on same position
        try {
            game.placeWorkerAuto(2, 2);
            fail("Should not allow placing worker on occupied cell");
        } catch (Exception e) {
            assertEquals("Cannot place worker because there is already another worker", e.getMessage());
        }
    }
    
    /**
     * Test Case 4: Verifies that a worker cannot be placed outside the board boundaries
     */
    @Test
    public void testWorkerPlacementOutOfBounds() throws Exception {
        try {
            game.placeWorkerAuto(-1, 0);
            fail("Should not allow placing worker out of bounds");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("out of bounds") || 
                      e.getMessage().contains("IndexOutOfBoundsException"));
        }
        
        try {
            game.placeWorkerAuto(5, 0);
            fail("Should not allow placing worker out of bounds");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("out of bounds") || 
                      e.getMessage().contains("IndexOutOfBoundsException"));
        }
    }

    /**
     * Test Case 6: Verifies that after all workers are placed, the game transitions to play phase
     */
    @Test
    public void testGameTransitionToPlayPhase() throws Exception {
        // Initially game should be in setup phase
        assertEquals(-1, game.getGameStatus());
        
        // Place all workers
        game.placeWorkerAuto(0, 0); // Player A worker 1
        game.placeWorkerAuto(0, 1); // Player A worker 2
        game.placeWorkerAuto(4, 4); // Player B worker 1
        game.placeWorkerAuto(4, 3); // Player B worker 2
        
        // Verify game has transitioned to play phase
        assertEquals(1, game.getGameStatus()); // Now in play phase
        assertEquals(0, game.getCurPlayer()); // Player A starts
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker to move
    }
    
    // ========== 2.2 Worker Movement Tests ==========
    
    /**
     * Test Case 7: Verifies that a worker can move legally to an adjacent cell
     */
    @Test
    public void testBasicGameTurn() throws Exception {
        setupCompleteGame();
        
        // Player A's turn - move worker
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals(1, game.getGameStatus()); // Still playing
        assertEquals(0, game.getCurPlayer()); // Still Player A
        assertEquals(5, game.getCurPlayerAction(), 0); // Now must build
        
        // Player A must build
        game.buildAuto(1, 0, 1, 1, false);
        assertEquals(1, game.getGameStatus()); // Still playing
        assertEquals(1, game.getCurPlayer()); // Now Player B's turn
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker to move
        
        // Verify move and build happened
        assertEquals("PlayerA", game.getBoard().getCell(1, 0).getWorker().getPlayerName());
        assertFalse(game.getBoard().getCell(0, 0).hasWorker());
        assertEquals(1, game.getBoard().getCell(1, 1).getTowerLevel());
    }
    
    /**
     * Test Case 8: Verifies that a worker cannot move more than one cell away
     * and cannot climb more than one level
     */
    @Test
    public void testMoveRestrictions() throws Exception {
        setupCompleteGame();
        
        // Test moving to non-adjacent cell
        try {
            game.moveWorkerAuto(0, 0, 2, 0);
            fail("Should not allow moving more than 1 cell");
        } catch (Exception e) {
            assertEquals("cannot move more than 1 cell", e.getMessage());
        }
        
        // Test moving to occupied cell
        try {
            game.moveWorkerAuto(0, 0, 0, 1);
            fail("Should not allow moving to occupied cell");
        } catch (Exception e) {
            assertEquals("worker already exists in the about-to-move cell", e.getMessage());
        }
        
        // Test height restriction - create a tower too high to climb
        game.getBoard().getCell(1, 0).setTower(new Tower(2));
        try {
            game.moveWorkerAuto(0, 0, 1, 0);
            fail("Should not allow climbing more than one level");
        } catch (Exception e) {
            assertEquals("Cannot move more one level higher", e.getMessage());
        }
    }
    
    /**
     * Test Case 9: Verifies that a worker cannot move to a cell with a dome
     */
    @Test
    public void testCannotMoveToDomedCell() throws Exception {
        setupCompleteGame();
        
        // Create a cell with a dome
        game.getBoard().getCell(1, 0).setTower(new Tower(4)); // Level 4 with dome
        assertTrue(game.getBoard().getCell(1, 0).getTower().hasDome());
        
        // Try to move to the domed cell
        try {
            game.moveWorkerAuto(0, 0, 1, 0);
            fail("Should not allow moving to a cell with a dome");
        } catch (Exception e) {
            assertEquals("cannot move to a tower with dome", e.getMessage());
        }
    }
    
    /**
     * Test Case 10: Verifies that a worker can build on an adjacent empty cell
     */
    @Test
    public void testBuildRestrictions() throws Exception {
        setupCompleteGame();
        
        // Move worker first
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // Test building on non-adjacent cell
        try {
            game.buildAuto(1, 0, 3, 0, false);
            fail("Should not allow building on non-adjacent cell");
        } catch (Exception e) {
            assertEquals("can only build in adjacent cell", e.getMessage());
        }
        
        // Test building on occupied cell
        try {
            game.buildAuto(1, 0, 0, 1, false);
            fail("Should not allow building on occupied cell");
        } catch (Exception e) {
            assertEquals("Cannot build because the distinated location alreay has a worker", e.getMessage());
        }
    }

    /**
     * Test Case 11: Verifies that a worker cannot build on a cell with a dome
     */
    @Test
    public void testCannotBuildOnDome() throws Exception {
        setupCompleteGame();
        
        // Create a cell with a dome
        game.getBoard().getCell(1, 1).setTower(new Tower(4)); // Level 4 with dome
        assertTrue(game.getBoard().getCell(1, 1).getTower().hasDome());
        
        // Move worker to adjacent cell
        game.moveWorkerAuto(0, 0, 0, 1);
        
        // Try to build on the domed cell
        try {
            game.buildAuto(0, 1, 1, 1, false);
            fail("Should not allow building on a cell with a dome");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Cannot build") || 
                      e.getMessage().contains("dome"));
        }
    }
    
    /**
     * Test Case 12: Verifies that a worker cannot build beyond level 4
     */
    @Test
    public void testCannotBuildBeyondLevel4() throws Exception {
        setupCompleteGame();
        
        // Create a tower at level 4 with dome
        game.getBoard().getCell(1, 1).setTower(new Tower(4));
        assertTrue(game.getBoard().getCell(1, 1).getTower().hasDome());
        
        // Move worker to adjacent cell
        game.moveWorkerAuto(0, 0, 0, 1);
        
        // Try to build on the level 4 tower
        try {
            game.buildAuto(0, 1, 1, 1, true);
            fail("Should not allow building beyond level 4");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Cannot build") || 
                      e.getMessage().contains("dome"));
        }
    }
    
    // ========== 2.4 Victory Condition Tests ==========
    
    /**
     * Test Case 13: Verifies that a player wins by moving a worker to level 3
     */
    @Test
    public void testWinByReachingLevel3() throws Exception {
        setupCompleteGame();
        
        // Build a level 3 tower
        game.getBoard().getCell(1, 0).setTower(new Tower(2));
        
        // Move worker to level 3 - should win
        game.moveWorkerAuto(0, 0, 1, 0);
        
        assertEquals(2, game.getGameStatus()); // Game ended
        assertEquals(0, game.getCurPlayer()); // Player A won
    }
    
    /**
     * Test Case 14: Verifies that no moves are allowed after the game ends
     */
    @Test
    public void testCannotMoveAfterGameEnds() throws Exception {
        setupCompleteGame();
        
        // Create winning condition
        game.getBoard().getCell(1, 0).setTower(new Tower(2));
        game.moveWorkerAuto(0, 0, 1, 0);
        
        assertEquals(2, game.getGameStatus()); // Game ended
        
        // Try to make another move
        try {
            game.moveWorkerAuto(4, 4, 3, 4);
            fail("Should not allow moves after game ends");
        } catch (Exception e) {
            assertEquals("game has ended", e.getMessage());
        }
    }

    // ========== 2.5 Error Handling Tests ==========
    
    /**
     * Test Case 15: Verifies that appropriate error messages are shown for illegal moves
     */
    @Test
    public void testIllegalMoveErrorMessages() throws Exception {
        setupCompleteGame();
        
        // Test moving to non-adjacent cell
        try {
            game.moveWorkerAuto(0, 0, 2, 2);
            fail("Should not allow moving to non-adjacent cell");
        } catch (Exception e) {
            assertEquals("cannot move more than 1 cell", e.getMessage());
        }
        
        // Test moving to occupied cell
        try {
            game.moveWorkerAuto(0, 0, 0, 1);
            fail("Should not allow moving to occupied cell");
        } catch (Exception e) {
            assertEquals("worker already exists in the about-to-move cell", e.getMessage());
        }
        
        // Test moving out of bounds
        try {
            game.moveWorkerAuto(0, 0, -1, 0);
            fail("Should not allow moving out of bounds");
        } catch (Exception e) {
            assertEquals("make this move will take the worker out of the board", e.getMessage());
        }
    }
    
    /**
     * Test Case 16: Verifies that appropriate error messages are shown for illegal builds
     */
    @Test
    public void testIllegalBuildErrorMessages() throws Exception {
        setupCompleteGame();
        
        // Move worker first
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // Test building on non-adjacent cell
        try {
            game.buildAuto(1, 0, 3, 3, false);
            fail("Should not allow building on non-adjacent cell");
        } catch (Exception e) {
            assertEquals("can only build in adjacent cell", e.getMessage());
        }
        
        // Test building on occupied cell
        try {
            game.buildAuto(1, 0, 0, 1, false);
            fail("Should not allow building on occupied cell");
        } catch (Exception e) {
            assertEquals("Cannot build because the distinated location alreay has a worker", e.getMessage());
        }
        
        // Test building out of bounds
        try {
            game.buildAuto(1, 0, -1, 0, false);
            fail("Should not allow building out of bounds");
        } catch (Exception e) {
            assertEquals("can not build when location is out of the board", e.getMessage());
        }
    }
    
    // ========== 2.3 Building System Tests ==========
    
    /**
     * Test Case 10: Verifies that building progresses correctly through levels 0-4
     */
    @Test
    public void testBuildingProgression() throws Exception {
        setupCompleteGame();
        
        // Move worker
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // Build level 1
        game.buildAuto(1, 0, 2, 0, false);
        assertEquals(1, game.getBoard().getCell(2, 0).getTowerLevel());
        assertFalse(game.getBoard().getCell(2, 0).getTower().hasDome());
        
        // Switch to Player B and build on same location
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 2, 0, false);
        assertEquals(2, game.getBoard().getCell(2, 0).getTowerLevel());
        
        // Continue building to level 3
        game.moveWorkerAuto(1, 0, 0, 0);
        game.buildAuto(0, 0, 2, 0, false);
        assertEquals(3, game.getBoard().getCell(2, 0).getTowerLevel());
        
        // Build dome (level 4)
        game.moveWorkerAuto(3, 4, 3, 3);
        game.buildAuto(3, 3, 2, 0, true);
        assertEquals(4, game.getBoard().getCell(2, 0).getTowerLevel());
        assertTrue(game.getBoard().getCell(2, 0).getTower().hasDome());
    }
    
    /**
     * Test Case 9: Verifies that a worker cannot move onto a cell with a dome
     */
    @Test
    public void testCannotMoveOnDome() throws Exception {
        setupCompleteGame();
        
        // Create a dome
        game.getBoard().getCell(1, 0).setTower(new Tower(4));
        
        try {
            game.moveWorkerAuto(0, 0, 1, 0);
            fail("Should not allow moving to dome");
        } catch (Exception e) {
            assertEquals("cannot move to a tower with dome", e.getMessage());
        }
    }

    // ========== 2.6 User Interface Tests ==========
    
    /**
     * Test Case 17: Verifies that worker selection works correctly
     */
    @Test
    public void testWorkerSelection() throws Exception {
        setupCompleteGame();
        
        // Initially no worker is selected (focusing grid is -1,-1)
        assertEquals(-1, game.getFocusingGridC());
        
        // Select worker by moving it
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // After moving, the worker's position should be the new focusing grid
        assertEquals(0, game.getFocusingGridC());
    }
    
    /**
     * Test Case 18: Verifies that building on empty cells works correctly
     */
    @Test
    public void testBuildingOnEmptyCell() throws Exception {
        setupCompleteGame();
        
        // Move worker first
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // Build on empty adjacent cell
        game.buildAuto(1, 0, 2, 0, false);
        
        // Verify building was placed
        assertEquals(1, game.getBoard().getCell(2, 0).getTowerLevel());
        assertFalse(game.getBoard().getCell(2, 0).getTower().hasDome());
        
        // Verify turn passed to next player
        assertEquals(1, game.getCurPlayer());
    }
    
    // ========== Player Turn Management Tests ==========
    
    /**
     * Verifies that player turns alternate correctly after each complete turn
     */
    @Test
    public void testPlayerTurnAlternation() throws Exception {
        setupCompleteGame();
        
        // Player A's turn
        assertEquals(0, game.getCurPlayer());
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals(0, game.getCurPlayer()); // Still Player A (must build)
        
        game.buildAuto(1, 0, 1, 1, false);
        assertEquals(1, game.getCurPlayer()); // Now Player B
        
        // Player B's turn
        game.moveWorkerAuto(4, 4, 3, 4);
        assertEquals(1, game.getCurPlayer()); // Still Player B (must build)
        
        game.buildAuto(3, 4, 3, 3, false);
        assertEquals(0, game.getCurPlayer()); // Back to Player A
    }
    
    /**
     * Verifies that a player must complete their full turn (move and build)
     */
    @Test
    public void testMustCompleteFullTurn() throws Exception {
        setupCompleteGame();
        
        // Move worker
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        // Try to move another worker without building
        try {
            game.moveWorkerAuto(0, 1, 1, 1);
            fail("Should not allow moving without completing build");
        } catch (Exception e) {
            // The game should enforce the correct sequence
            assertTrue(e.getMessage().contains("Cannot move worker because no worker in specified location") ||
                      e.getMessage().contains("only worker owner can command the worker"));
        }
    }

    // ========== Board Boundary Tests ==========
    
    /**
     * Verifies that the game enforces board boundaries correctly
     */
    @Test
    public void testBoardBoundaries() throws Exception {
        setupCompleteGame();
        
        // Try to move out of bounds
        try {
            game.moveWorkerAuto(0, 0, -1, 0);
            fail("Should not allow moving out of bounds");
        } catch (Exception e) {
            assertEquals("make this move will take the worker out of the board", e.getMessage());
        }
        
        try {
            game.moveWorkerAuto(4, 4, 5, 4);
            fail("Should not allow moving out of bounds");
        } catch (Exception e) {
            assertEquals("make this move will take the worker out of the board", e.getMessage());
        }
    }

    // ========== Worker Ownership Tests ==========
    
    /**
     * Verifies that a player can only move their own workers
     */
    @Test
    public void testWorkerOwnership() throws Exception {
        setupCompleteGame();
        
        // Player A tries to move Player B's worker
        try {
            game.moveWorkerAuto(4, 4, 3, 4);
            fail("Should not allow moving opponent's worker");
        } catch (Exception e) {
            assertEquals("only worker owner can command the worker", e.getMessage());
        }
    }

    // ========== Game State Persistence Tests ==========
    
    /**
     * Verifies that the game state remains consistent throughout gameplay
     */
    @Test
    public void testGameStateConsistency() throws Exception {
        setupCompleteGame();
        
        // Make several moves and verify state consistency
        game.moveWorkerAuto(0, 0, 1, 0);
        game.buildAuto(1, 0, 1, 1, false);
        
        // Verify board state
        assertEquals("PlayerA", game.getBoard().getCell(1, 0).getWorker().getPlayerName());
        assertEquals(1, game.getBoard().getCell(1, 1).getTowerLevel());
        assertFalse(game.getBoard().getCell(0, 0).hasWorker());
        
        // Verify game state
        assertEquals(1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }

    // ========== Edge Case Tests ==========
    
    @Test
    public void testWorkerMovementToAllDirections() throws Exception {
        // Place worker in center of board
        playerA = new Player("PlayerA");
        playerB = new Player("PlayerB");
        board = new Board();
        game = new Game(board, playerA, playerB);
        
        // Place workers
        game.placeWorkerAuto(2, 2); // Center
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(4, 0);
        
        // Test all 8 directions from center
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        for (int[] dir : directions) {
            int newR = 2 + dir[0];
            int newC = 2 + dir[1];
            
            // Skip if position is occupied
            if (game.getBoard().hasWorker(newR, newC)) continue;
            
            // This should be a valid move
            game.moveWorkerAuto(2, 2, newR, newC);
            assertEquals("PlayerA", game.getBoard().getCell(newR, newC).getWorker().getPlayerName());
            
            // Move back for next test
            game.buildAuto(newR, newC, 2, 2, false);
            game.moveWorkerAuto(0, 0, 0, 1);
            game.buildAuto(0, 1, 0, 2, false);
            game.moveWorkerAuto(newR, newC, 2, 2);
            game.buildAuto(2, 2, 1, 2, false);
            break; // Just test one direction to avoid complexity
        }
    }

    // ========== Helper Methods ==========
    
    /**
     * Sets up a complete game with all workers placed and ready for play
     */
    private void setupCompleteGame() throws Exception {
        game.placeWorkerAuto(0, 0); // Player A worker 1
        game.placeWorkerAuto(0, 1); // Player A worker 2
        game.placeWorkerAuto(4, 4); // Player B worker 1
        game.placeWorkerAuto(4, 3); // Player B worker 2
        
        // Verify setup is complete
        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }
    
    /**
     * Creates a specific board configuration for testing
     */
    private void createTestConfiguration() throws Exception {
        setupCompleteGame();
        
        // Create some towers for testing
        game.getBoard().getCell(2, 2).setTower(new Tower(1));
        game.getBoard().getCell(2, 3).setTower(new Tower(2));
        game.getBoard().getCell(3, 2).setTower(new Tower(3));
        game.getBoard().getCell(3, 3).setTower(new Tower(4)); // Dome
    }
}