package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for user interface requirements and interactions
 * Based on requirements document sections 3.2, 3.3, and 3.4
 */
public class UserInterfaceTest {
    
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

    // ========== 3.2 User Interface Tests ==========
    
    @Test
    public void testBoardDisplay() {
        // Test that board displays 5x5 grid
        assertEquals(5, board.getSize());
        
        // Test that each cell can display tower levels
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                Cell cell = board.getCell(r, c);
                assertNotNull(cell);
                assertEquals(0, cell.getTowerLevel()); // Initially 0
                assertFalse(cell.hasWorker()); // Initially no worker
            }
        }
    }
    
    @Test
    public void testWorkerPositionDisplay() throws Exception {
        // Place workers and verify they can be displayed
        game.placeWorkerAuto(1, 1);
        game.placeWorkerAuto(2, 2);
        game.placeWorkerAuto(3, 3);
        game.placeWorkerAuto(4, 4);
        
        // Verify workers are visible on board
        assertTrue(board.hasWorker(1, 1));
        assertTrue(board.hasWorker(2, 2));
        assertTrue(board.hasWorker(3, 3));
        assertTrue(board.hasWorker(4, 4));
        
        // Verify worker ownership is displayed
        assertEquals("PlayerA", board.getCell(1, 1).getWorker().getPlayerName());
        assertEquals("PlayerA", board.getCell(2, 2).getWorker().getPlayerName());
        assertEquals("PlayerB", board.getCell(3, 3).getWorker().getPlayerName());
        assertEquals("PlayerB", board.getCell(4, 4).getWorker().getPlayerName());
    }
    
    @Test
    public void testTowerLevelDisplay() throws Exception {
        setupGameInProgress();
        
        // Build towers of different levels
        game.moveWorkerAuto(1, 1, 0, 1);
        game.buildAuto(0, 1, 0, 0, false); // Level 1
        
        completeTurn();
        game.moveWorkerAuto(3, 3, 2, 3);
        game.buildAuto(2, 3, 0, 0, false); // Level 2
        
        completeTurn();
        game.moveWorkerAuto(0, 1, 1, 1);
        game.buildAuto(1, 1, 0, 0, false); // Level 3
        
        completeTurn();
        game.moveWorkerAuto(2, 3, 3, 3);
        game.buildAuto(3, 3, 0, 0, true); // Level 4 (dome)
        
        // Verify tower levels are correctly displayed
        assertEquals(4, board.getCell(0, 0).getTowerLevel());
        assertTrue(board.getCell(0, 0).getTower().hasDome());
    }

    // ========== 3.3 User Interaction Tests ==========
    
    @Test
    public void testWorkerSelectionAndMovement() throws Exception {
        setupGameInProgress();
        
        // Test selecting and moving worker
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker phase
        
        // Move worker (simulates clicking worker then target)
        game.moveWorkerAuto(1, 1, 1, 2);
        
        // Verify worker moved
        assertFalse(board.hasWorker(1, 1));
        assertTrue(board.hasWorker(1, 2));
        assertEquals("PlayerA", board.getCell(1, 2).getWorker().getPlayerName());
        
        // Should now be in build phase
        assertEquals(5, game.getCurPlayerAction(), 0);
    }
    
    @Test
    public void testBuildingSelection() throws Exception {
        setupGameInProgress();
        
        // Move worker first
        game.moveWorkerAuto(1, 1, 1, 2);
        
        // Test building selection (simulates clicking build location)
        game.buildAuto(1, 2, 2, 2, false);
        
        // Verify building was placed
        assertEquals(1, board.getCell(2, 2).getTowerLevel());
        
        // Should now be next player's turn
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }
    
    @Test
    public void testDomeBuilding() throws Exception {
        setupGameInProgress();
        
        // Create a level 3 tower
        board.getCell(2, 2).setTower(new Tower(3));
        
        // Move worker
        game.moveWorkerAuto(1, 1, 1, 2);
        
        // Build dome
        game.buildAuto(1, 2, 2, 2, true);
        
        // Verify dome was built
        assertEquals(4, board.getCell(2, 2).getTowerLevel());
        assertTrue(board.getCell(2, 2).getTower().hasDome());
    }

    // ========== 3.4 Game State Management Tests ==========
    
    @Test
    public void testTurnManagement() throws Exception {
        setupGameInProgress();
        
        // Initial state
        assertEquals(0, game.getCurPlayer()); // Player A
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker
        
        // Player A's move
        game.moveWorkerAuto(1, 1, 1, 2);
        assertEquals(0, game.getCurPlayer()); // Still Player A
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        // Player A's build
        game.buildAuto(1, 2, 2, 2, false);
        assertEquals(1, game.getCurPlayer()); // Now Player B
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker
        
        // Player B's move
        game.moveWorkerAuto(3, 3, 3, 2);
        assertEquals(1, game.getCurPlayer()); // Still Player B
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        // Player B's build
        game.buildAuto(3, 2, 4, 2, false);
        assertEquals(0, game.getCurPlayer()); // Back to Player A
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker
    }
    
    @Test
    public void testGameStatusTracking() throws Exception {
        // Initial status should be setup phase
        assertEquals(-1, game.getGameStatus());
        
        // Place workers
        game.placeWorkerAuto(1, 1);
        assertEquals(-1, game.getGameStatus()); // Still setup
        
        game.placeWorkerAuto(2, 2);
        assertEquals(-1, game.getGameStatus()); // Still setup
        
        game.placeWorkerAuto(3, 3);
        assertEquals(-1, game.getGameStatus()); // Still setup
        
        game.placeWorkerAuto(4, 4);
        assertEquals(1, game.getGameStatus()); // Now playing
        
        // Create win condition
        board.getCell(0, 0).setTower(new Tower(2));
        game.moveWorkerAuto(1, 1, 0, 0);
        assertEquals(2, game.getGameStatus()); // Game ended
    }
    
    @Test
    public void testGameReset() throws Exception {
        // Play a partial game
        setupGameInProgress();
        game.moveWorkerAuto(1, 1, 1, 2);
        game.buildAuto(1, 2, 2, 2, false);
        
        // Create new game (simulates reset)
        playerA = new Player("PlayerA");
        playerB = new Player("PlayerB");
        board = new Board();
        game = new Game(board, playerA, playerB);
        
        // Verify reset state
        assertEquals(-1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(0, game.getCurPlayerAction(), 0);
        
        // Verify board is clear
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                assertFalse(board.hasWorker(r, c));
                assertEquals(0, board.getCell(r, c).getTowerLevel());
            }
        }
    }

    // ========== Error Handling and User Feedback Tests ==========
    
    @Test
    public void testInvalidMoveErrorMessages() throws Exception {
        setupGameInProgress();
        
        // Test moving non-existent worker
        try {
            game.moveWorkerAuto(0, 0, 0, 1);
            fail("Should provide error for moving non-existent worker");
        } catch (Exception e) {
            assertEquals("Cannot move worker because no worker in specified location", e.getMessage());
        }
        
        // Test moving to occupied cell
        try {
            game.moveWorkerAuto(1, 1, 2, 2);
            fail("Should provide error for moving to occupied cell");
        } catch (Exception e) {
            assertEquals("worker already exists in the about-to-move cell", e.getMessage());
        }
        
        // Test moving too far
        try {
            game.moveWorkerAuto(1, 1, 3, 3);
            fail("Should provide error for moving too far");
        } catch (Exception e) {
            assertEquals("cannot move more than 1 cell", e.getMessage());
        }
        
        // Test moving opponent's worker
        try {
            game.moveWorkerAuto(3, 3, 3, 2);
            fail("Should provide error for moving opponent's worker");
        } catch (Exception e) {
            assertEquals("only worker owner can command the worker", e.getMessage());
        }
    }
    
    @Test
    public void testInvalidBuildErrorMessages() throws Exception {
        setupGameInProgress();
        
        // Move worker first
        game.moveWorkerAuto(1, 1, 1, 2);
        
        // Test building on occupied cell
        try {
            game.buildAuto(1, 2, 2, 2, false);
            fail("Should provide error for building on occupied cell");
        } catch (Exception e) {
            assertEquals("Cannot build because the distinated location alreay has a worker", e.getMessage());
        }
        
        // Test building too far
        try {
            game.buildAuto(1, 2, 4, 4, false);
            fail("Should provide error for building too far");
        } catch (Exception e) {
            assertEquals("can only build in adjacent cell", e.getMessage());
        }
        
        // Test building out of bounds
        try {
            game.buildAuto(1, 2, -1, 2, false);
            fail("Should provide error for building out of bounds");
        } catch (Exception e) {
            assertEquals("can not build when location is out of the board", e.getMessage());
        }
    }
    
    @Test
    public void testWorkerPlacementErrorMessages() throws Exception {
        // Place first worker
        game.placeWorkerAuto(2, 2);
        
        // Try to place on same location
        try {
            game.placeWorkerAuto(2, 2);
            fail("Should provide error for placing on occupied cell");
        } catch (Exception e) {
            assertEquals("Cannot place worker because there is already another worker", e.getMessage());
        }
    }

    // ========== Game Flow and State Validation Tests ==========
    
    @Test
    public void testCompleteGameFlow() throws Exception {
        // Test complete game flow from start to finish
        
        // 1. Worker placement phase
        assertEquals(-1, game.getGameStatus());
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(4, 3);
        assertEquals(1, game.getGameStatus());
        
        // 2. Play phase - several turns
        for (int turn = 0; turn < 3; turn++) {
            // Player A's turn
            assertEquals(0, game.getCurPlayer());
            assertEquals(2, game.getCurPlayerAction(), 0);
            
            game.moveWorkerAuto(0, 0, 1, 0);
            assertEquals(5, game.getCurPlayerAction(), 0);
            
            game.buildAuto(1, 0, 1, 1, false);
            assertEquals(1, game.getCurPlayer());
            
            // Player B's turn
            assertEquals(2, game.getCurPlayerAction(), 0);
            
            game.moveWorkerAuto(4, 4, 3, 4);
            assertEquals(5, game.getCurPlayerAction(), 0);
            
            game.buildAuto(3, 4, 3, 3, false);
            assertEquals(0, game.getCurPlayer());
            
            // Reset positions for next iteration
            game.moveWorkerAuto(1, 0, 0, 0);
            game.buildAuto(0, 0, 0, 2, false);
            game.moveWorkerAuto(3, 4, 4, 4);
            game.buildAuto(4, 4, 4, 2, false);
        }
        
        // 3. Win condition
        board.getCell(1, 0).setTower(new Tower(2));
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals(2, game.getGameStatus()); // Game ended
    }

    // ========== Helper Methods ==========
    
    private void setupGameInProgress() throws Exception {
        game.placeWorkerAuto(1, 1); // Player A worker 1
        game.placeWorkerAuto(2, 2); // Player A worker 2
        game.placeWorkerAuto(3, 3); // Player B worker 1
        game.placeWorkerAuto(4, 4); // Player B worker 2
        
        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
    }
    
    private void completeTurn() throws Exception {
        if (game.getCurPlayerAction() == 5.0) {
            int currentPlayer = game.getCurPlayer();
            if (currentPlayer == 0) {
                // Find a valid build location for Player A
                if (board.hasWorker(1, 2)) {
                    game.buildAuto(1, 2, 0, 2, false);
                } else if (board.hasWorker(0, 1)) {
                    game.buildAuto(0, 1, 0, 2, false);
                } else {
                    game.buildAuto(1, 1, 0, 1, false);
                }
            } else {
                // Find a valid build location for Player B
                if (board.hasWorker(2, 3)) {
                    game.buildAuto(2, 3, 1, 3, false);
                } else if (board.hasWorker(3, 3)) {
                    game.buildAuto(3, 3, 3, 2, false);
                } else {
                    game.buildAuto(4, 4, 4, 3, false);
                }
            }
        }
    }
}