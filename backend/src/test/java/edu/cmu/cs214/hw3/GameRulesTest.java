package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class focused on specific game rules and edge cases
 * Based on requirements document section 3.1 (Game Rules)
 */
public class GameRulesTest {
    
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

    // ========== 3.1.1 Game Objective Tests ==========
    
    @Test
    public void testWinConditionLevel3() throws Exception {
        setupGameInProgress();
        
        // Place worker on level 2 tower first, then create level 3 tower adjacent
        game.getBoard().getCell(0, 0).setTower(new Tower(2));
        game.getBoard().getCell(1, 0).setTower(new Tower(3));
        
        // Move worker from level 2 to level 3 tower (should trigger win)
        game.moveWorkerAuto(0, 0, 1, 0);
        
        assertEquals(2, game.getGameStatus()); // Game should end
        assertEquals(0, game.getCurPlayer()); // Player A should be winner
    }
    
    @Test
    public void testNoWinOnLevel2() throws Exception {
        setupGameInProgress();
        
        // Create a level 1 tower adjacent to worker at (0,1)
        game.getBoard().getCell(1, 1).setTower(new Tower(1));
        
        // Move worker to level 2 (should NOT trigger win)
        game.moveWorkerAuto(0, 1, 1, 1);
        
        assertEquals(1, game.getGameStatus()); // Game should continue
        assertEquals(0, game.getCurPlayer()); // Still Player A's turn (must build)
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
    }

    // ========== 3.1.2 Game Components Tests ==========
    
    @Test
    public void testBoardSize() {
        // Verify board is 5x5
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                assertNotNull(game.getBoard().getCell(r, c));
            }
        }
        
        // Verify out of bounds throws exception
        try {
            game.getBoard().getCell(5, 0);
            fail("Should throw exception for out of bounds access");
        } catch (Exception e) {
            // Expected
        }
    }
    
    @Test
    public void testWorkerCount() throws Exception {
        setupGameInProgress();
        
        // Each player should have exactly 2 workers
        assertNotNull(playerA.getWorkerA());
        assertNotNull(playerA.getWorkerB());
        assertNotNull(playerB.getWorkerA());
        assertNotNull(playerB.getWorkerB());
    }
    
    @Test
    public void testBuildingLevels() throws Exception {
        setupGameInProgress();
        
        // Move worker to build
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // Test building progression: 0 -> 1 -> 2 -> 3 -> 4(dome)
        Cell testCell = game.getBoard().getCell(2, 2);
        
        // Level 0 to 1
        game.buildAuto(1, 0, 2, 2, false);
        assertEquals(1, testCell.getTowerLevel());
        assertFalse(testCell.getTower().hasDome());
        
        // Continue building on same cell through multiple turns
        completeTurn(); // Finish Player A's turn
        game.moveWorkerAuto(4, 4, 3, 4);
        game.buildAuto(3, 4, 2, 2, false); // Level 1 to 2
        assertEquals(2, testCell.getTowerLevel());
        
        completeTurn(); // Finish Player B's turn
        game.moveWorkerAuto(1, 0, 0, 0);
        game.buildAuto(0, 0, 2, 2, false); // Level 2 to 3
        assertEquals(3, testCell.getTowerLevel());
        
        completeTurn(); // Finish Player A's turn
        game.moveWorkerAuto(3, 4, 3, 3);
        game.buildAuto(3, 3, 2, 2, true); // Level 3 to 4 (dome)
        assertEquals(4, testCell.getTowerLevel());
        assertTrue(testCell.getTower().hasDome());
    }

    // ========== 3.1.4 Game Turn Tests ==========
    
    @Test
    public void testMandatoryMoveAndBuild() throws Exception {
        setupGameInProgress();
        
        // Player must move first
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker
        
        // Move worker
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        // Must build before turn ends
        game.buildAuto(1, 0, 1, 1, false);
        assertEquals(1, game.getCurPlayer()); // Turn passed to Player B
    }
    
    @Test
    public void testAdjacentMovementOnly() throws Exception {
        setupGameInProgress();
        
        // Test all valid adjacent moves (8 directions)
        int[][] validMoves = {
            {0, 0, 0, 1}, {0, 0, 1, 0}, {0, 0, 1, 1}
        };
        
        for (int[] move : validMoves) {
            if (!game.getBoard().hasWorker(move[2], move[3])) {
                game.moveWorkerAuto(move[0], move[1], move[2], move[3]);
                assertEquals("PlayerA", game.getBoard().getCell(move[2], move[3]).getWorker().getPlayerName());
                
                // Reset for next test
                game.buildAuto(move[2], move[3], move[2] + 1, move[3], false);
                completeTurn();
                game.moveWorkerAuto(move[2], move[3], move[0], move[1]);
                game.buildAuto(move[0], move[1], move[0], move[1] + 1, false);
                completeTurn();
                break; // Test one valid move
            }
        }
        
        // Test invalid non-adjacent move
        try {
            game.moveWorkerAuto(0, 0, 2, 2);
            fail("Should not allow non-adjacent move");
        } catch (Exception e) {
            assertEquals("cannot move more than 1 cell", e.getMessage());
        }
    }
    
    @Test
    public void testHeightRestrictions() throws Exception {
        setupGameInProgress();
        
        // Test climbing one level (should work)
        game.getBoard().getCell(1, 0).setTower(new Tower(1));
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals("PlayerA", game.getBoard().getCell(1, 0).getWorker().getPlayerName());
        
        // Reset
        game.buildAuto(1, 0, 1, 1, false);
        completeTurn();
        game.moveWorkerAuto(1, 0, 0, 0);
        game.buildAuto(0, 0, 0, 1, false);
        completeTurn();
        
        // Test climbing two levels (should fail)
        game.getBoard().getCell(1, 0).setTower(new Tower(2));
        try {
            game.moveWorkerAuto(0, 0, 1, 0);
            fail("Should not allow climbing more than one level");
        } catch (Exception e) {
            assertEquals("Cannot move more one level higher", e.getMessage());
        }
        
        // Test descending any number of levels (should work)
        game.getBoard().getCell(0, 0).setTower(new Tower(3));
        game.getBoard().getCell(1, 0).setTower(new Tower(0));
        game.moveWorkerAuto(0, 0, 1, 0); // 3 -> 0 should work
        assertEquals("PlayerA", game.getBoard().getCell(1, 0).getWorker().getPlayerName());
    }
    
    @Test
    public void testBuildingRestrictions() throws Exception {
        setupGameInProgress();
        
        // Move worker
        game.moveWorkerAuto(0, 0, 1, 0);
        
        // Test building on adjacent cell (should work)
        game.buildAuto(1, 0, 1, 1, false);
        assertEquals(1, game.getBoard().getCell(1, 1).getTowerLevel());
        
        completeTurn();
        game.moveWorkerAuto(4, 4, 3, 4);
        
        // Test building on non-adjacent cell (should fail)
        try {
            game.buildAuto(3, 4, 1, 1, false);
            fail("Should not allow building on non-adjacent cell");
        } catch (Exception e) {
            assertEquals("can only build in adjacent cell", e.getMessage());
        }
    }

    // ========== 3.1.5 Game Operation Sequence Tests ==========
    
    @Test
    public void testCorrectOperationSequence() throws Exception {
        setupGameInProgress();
        
        // 1. Select worker (implicit in moveWorkerAuto)
        // 2. Move worker
        game.moveWorkerAuto(0, 0, 1, 0);
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        // 3. Build
        game.buildAuto(1, 0, 1, 1, false);
        assertEquals(1, game.getCurPlayer()); // Turn passed
        assertEquals(2, game.getCurPlayerAction(), 0); // Next player selects worker
    }

    // ========== 3.1.7 Invalid Operations Tests ==========
    
    @Test
    public void testInvalidOperations() throws Exception {
        setupGameInProgress();
        
        // Moving to occupied cell
        try {
            game.moveWorkerAuto(0, 0, 0, 1);
            fail("Should not allow moving to occupied cell");
        } catch (Exception e) {
            assertEquals("worker already exists in the about-to-move cell", e.getMessage());
        }
        
        // Moving to dome
        game.getBoard().getCell(1, 0).setTower(new Tower(4));
        try {
            game.moveWorkerAuto(0, 0, 1, 0);
            fail("Should not allow moving to dome");
        } catch (Exception e) {
            assertEquals("cannot move to a tower with dome", e.getMessage());
        }
        
        // Skipping build (not directly testable as buildAuto is required)
        game.moveWorkerAuto(0, 0, 1, 1);
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        // The game enforces building before turn can end
    }

    // ========== 3.1.8 Game End Conditions Tests ==========
    
    @Test
    public void testGameEndOnWin() throws Exception {
        setupGameInProgress();
        
        // Create winning condition
        game.getBoard().getCell(1, 0).setTower(new Tower(2));
        game.moveWorkerAuto(0, 0, 1, 0);
        
        assertEquals(2, game.getGameStatus()); // Game ended
        
        // No further moves should be allowed
        try {
            game.moveWorkerAuto(4, 4, 3, 4);
            fail("Should not allow moves after game ends");
        } catch (Exception e) {
            assertEquals("game has ended", e.getMessage());
        }
        
        try {
            game.buildAuto(1, 0, 1, 1, false);
            fail("Should not allow building after game ends");
        } catch (Exception e) {
            assertEquals("game has ended", e.getMessage());
        }
    }

    // ========== Performance and Boundary Tests ==========
    
    @Test
    public void testBoardBoundaries() throws Exception {
        setupGameInProgress();
        
        // Test all boundary positions
        int[][] boundaries = {
            {0, 0}, {0, 4}, {4, 0}, {4, 4}
        };
        
        for (int[] pos : boundaries) {
            // These positions should be valid
            assertNotNull(game.getBoard().getCell(pos[0], pos[1]));
        }
        
        // Test out of bounds
        int[][] outOfBounds = {
            {-1, 0}, {0, -1}, {5, 0}, {0, 5}, {-1, -1}, {5, 5}
        };
        
        for (int[] pos : outOfBounds) {
            try {
                game.getBoard().getCell(pos[0], pos[1]);
                fail("Should throw exception for out of bounds: " + pos[0] + "," + pos[1]);
            } catch (Exception e) {
                // Expected
            }
        }
    }
    
    @Test
    public void testGameStateConsistency() throws Exception {
        setupGameInProgress();
        
        // Make a series of moves and verify state remains consistent
        for (int i = 0; i < 5; i++) {
            int currentPlayer = game.getCurPlayer();
            double currentAction = game.getCurPlayerAction();
            
            // Make a move based on current state
            if (currentAction == 2.0) { // Select worker to move
                if (currentPlayer == 0) {
                    game.moveWorkerAuto(0, 0, 0, 2);
                } else {
                    game.moveWorkerAuto(4, 4, 4, 2);
                }
            } else if (currentAction == 5.0) { // Build
                if (currentPlayer == 0) {
                    game.buildAuto(0, 2, 1, 2, false);
                } else {
                    game.buildAuto(4, 2, 3, 2, false);
                }
            }
            
            // Verify game state is still valid
            assertTrue(game.getGameStatus() >= -1 && game.getGameStatus() <= 2);
            assertTrue(game.getCurPlayer() >= 0 && game.getCurPlayer() <= 1);
            assertTrue(game.getCurPlayerAction() >= 0);
            
            if (game.getGameStatus() == 2) break; // Game ended
        }
    }

    // ========== Helper Methods ==========
    
    private void setupGameInProgress() throws Exception {
        // Place all workers to get to play phase
        game.placeWorkerAuto(0, 0); // Player A worker 1
        game.placeWorkerAuto(0, 1); // Player A worker 2
        game.placeWorkerAuto(4, 4); // Player B worker 1
        game.placeWorkerAuto(4, 3); // Player B worker 2
        
        assertEquals(1, game.getGameStatus()); // Should be in play phase
        assertEquals(0, game.getCurPlayer()); // Player A starts
    }
    
    private void completeTurn() throws Exception {
        // Helper to complete current player's turn if they're in build phase
        if (game.getCurPlayerAction() == 5.0) {
            int currentPlayer = game.getCurPlayer();
            if (currentPlayer == 0) {
                // Find Player A's worker and build somewhere
                if (game.getBoard().hasWorker(1, 0)) {
                    game.buildAuto(1, 0, 2, 0, false);
                } else {
                    game.buildAuto(0, 0, 1, 0, false);
                }
            } else {
                // Find Player B's worker and build somewhere
                if (game.getBoard().hasWorker(3, 4)) {
                    game.buildAuto(3, 4, 2, 4, false);
                } else {
                    game.buildAuto(4, 4, 3, 4, false);
                }
            }
        }
    }
}