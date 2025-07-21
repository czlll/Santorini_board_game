package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for performance and non-functional requirements
 * Based on requirements document section 4 (Non-functional Requirements)
 */
public class PerformanceTest {
    
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

    // ========== 4.1 Performance Requirements Tests ==========
    
    @Test
    public void testGameInitializationTime() throws Exception {
        // Test that game initialization is fast (< 5 seconds as per requirements)
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            Player pA = new Player("PlayerA" + i);
            Player pB = new Player("PlayerB" + i);
            Board b = new Board();
            Game g = new Game(b, pA, pB);
            
            // Verify game is properly initialized
            assertNotNull(g);
            assertEquals(-1, g.getGameStatus());
            assertEquals(0, g.getCurPlayer());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 initializations well under 5 seconds
        assertTrue("Game initialization too slow: " + duration + "ms", duration < 5000);
    }
    
    @Test
    public void testGameOperationResponseTime() throws Exception {
        // Test that game operations respond quickly (< 1 second as per requirements)
        setupGameInProgress();
        
        long startTime = System.currentTimeMillis();
        
        // Perform 100 game operations
        for (int i = 0; i < 50; i++) {
            // Player A move and build
            game.moveWorkerAuto(1, 1, 1, 2);
            game.buildAuto(1, 2, 2, 2, false);
            
            // Player B move and build
            game.moveWorkerAuto(3, 3, 3, 2);
            game.buildAuto(3, 2, 4, 2, false);
            
            // Reset positions
            game.moveWorkerAuto(1, 2, 1, 1);
            game.buildAuto(1, 1, 0, 1, false);
            game.moveWorkerAuto(3, 2, 3, 3);
            game.buildAuto(3, 3, 3, 4, false);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 200 operations should complete well under 1 second each
        assertTrue("Game operations too slow: " + duration + "ms for 200 operations", 
                  duration < 1000);
    }
    
    @Test
    public void testBoardStateAccessTime() {
        // Test that board state access is fast
        long startTime = System.currentTimeMillis();
        
        // Access board state many times
        for (int i = 0; i < 10000; i++) {
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    Cell cell = board.getCell(r, c);
                    cell.getTowerLevel();
                    cell.hasWorker();
                    cell.getTower();
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 250,000 board accesses should be very fast
        assertTrue("Board access too slow: " + duration + "ms", duration < 1000);
    }

    // ========== 4.2 Security Requirements Tests ==========
    
    @Test
    public void testGameStateIntegrity() throws Exception {
        setupGameInProgress();
        
        // Test that game state cannot be corrupted by invalid operations
        int initialGameStatus = game.getGameStatus();
        int initialPlayer = game.getCurPlayer();
        double initialAction = game.getCurPlayerAction();
        
        // Try invalid operations
        try {
            game.moveWorkerAuto(1, 1, 3, 3); // Invalid move
            fail("Should not allow invalid move");
        } catch (Exception e) {
            // Verify game state is unchanged
            assertEquals(initialGameStatus, game.getGameStatus());
            assertEquals(initialPlayer, game.getCurPlayer());
            assertEquals(initialAction, game.getCurPlayerAction(), 0);
        }
        
        try {
            game.buildAuto(1, 1, 4, 4, false); // Invalid build (wrong phase)
            fail("Should not allow building in move phase");
        } catch (Exception e) {
            // Verify game state is unchanged
            assertEquals(initialGameStatus, game.getGameStatus());
            assertEquals(initialPlayer, game.getCurPlayer());
            assertEquals(initialAction, game.getCurPlayerAction(), 0);
        }
    }
    
    @Test
    public void testPlayerIsolation() throws Exception {
        setupGameInProgress();
        
        // Test that players cannot interfere with each other's workers
        try {
            game.moveWorkerAuto(3, 3, 2, 3); // Player A trying to move Player B's worker
            fail("Should not allow moving opponent's worker");
        } catch (Exception e) {
            assertEquals("only worker owner can command the worker", e.getMessage());
        }
        
        // Verify Player B's worker is unchanged
        assertTrue(board.hasWorker(3, 3));
        assertEquals("PlayerB", board.getCell(3, 3).getWorker().getPlayerName());
    }

    // ========== 4.3 Usability Requirements Tests ==========
    
    @Test
    public void testClearErrorMessages() throws Exception {
        setupGameInProgress();
        
        // Test that error messages are clear and helpful
        String[] expectedMessages = {
            "Cannot move worker because no worker in specified location",
            "worker already exists in the about-to-move cell",
            "cannot move more than 1 cell",
            "only worker owner can command the worker",
            "make this move will take the worker out of the board",
            "Cannot move more one level higher",
            "cannot move to a tower with dome"
        };
        
        // Test each error condition
        try {
            game.moveWorkerAuto(0, 0, 0, 1); // No worker at 0,0
        } catch (Exception e) {
            assertEquals(expectedMessages[0], e.getMessage());
        }
        
        try {
            game.moveWorkerAuto(1, 1, 2, 2); // Worker already at 2,2
        } catch (Exception e) {
            assertEquals(expectedMessages[1], e.getMessage());
        }
        
        try {
            game.moveWorkerAuto(1, 1, 3, 3); // Too far
        } catch (Exception e) {
            assertEquals(expectedMessages[2], e.getMessage());
        }
        
        try {
            game.moveWorkerAuto(3, 3, 3, 2); // Opponent's worker
        } catch (Exception e) {
            assertEquals(expectedMessages[3], e.getMessage());
        }
    }
    
    @Test
    public void testGameStateVisibility() throws Exception {
        // Test that game state is always accessible and clear
        
        // During setup phase
        assertEquals(-1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(0, game.getCurPlayerAction(), 0);
        
        // Place workers
        game.placeWorkerAuto(1, 1);
        assertEquals(0, game.getCurPlayer());
        assertEquals(1, game.getCurPlayerAction(), 0);
        
        game.placeWorkerAuto(2, 2);
        assertEquals(1, game.getCurPlayer());
        assertEquals(0, game.getCurPlayerAction(), 0);
        
        game.placeWorkerAuto(3, 3);
        assertEquals(1, game.getCurPlayer());
        assertEquals(1, game.getCurPlayerAction(), 0);
        
        game.placeWorkerAuto(4, 4);
        assertEquals(1, game.getGameStatus()); // Play phase
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
        
        // During play phase
        game.moveWorkerAuto(1, 1, 1, 0);
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0); // Must build
        
        game.buildAuto(1, 0, 0, 0, false);
        assertEquals(1, game.getCurPlayer()); // Next player
        assertEquals(2, game.getCurPlayerAction(), 0); // Select worker
    }

    // ========== 4.4 Compatibility Requirements Tests ==========
    
    @Test
    public void testCrossPlatformCompatibility() {
        // Test that game logic works consistently across different environments
        // This is mainly about ensuring no platform-specific dependencies
        
        // Test basic operations work
        assertNotNull(game);
        assertNotNull(board);
        assertNotNull(playerA);
        assertNotNull(playerB);
        
        // Test that all game operations are deterministic
        try {
            game.placeWorkerAuto(0, 0);
            game.placeWorkerAuto(0, 1);
            game.placeWorkerAuto(4, 4);
            game.placeWorkerAuto(4, 3);
            
            // Same operations should always produce same result
            assertEquals(1, game.getGameStatus());
            assertEquals(0, game.getCurPlayer());
            assertTrue(board.hasWorker(0, 0));
            assertTrue(board.hasWorker(0, 1));
            assertTrue(board.hasWorker(4, 4));
            assertTrue(board.hasWorker(4, 3));
        } catch (Exception e) {
            fail("Basic game operations should work on all platforms");
        }
    }

    // ========== 4.5 Maintainability Tests ==========
    
    @Test
    public void testCodeModularity() {
        // Test that game components are properly separated
        
        // Board should be independent
        Board testBoard = new Board();
        assertNotNull(testBoard);
        assertEquals(5, testBoard.getSize());
        
        // Players should be independent
        Player testPlayer = new Player("Test");
        assertNotNull(testPlayer);
        assertEquals("Test", testPlayer.getName());
        
        // Game should coordinate components
        assertNotNull(game.getBoard());
        assertNotNull(game.getPlayerA());
        assertNotNull(game.getPlayerB());
    }
    
    @Test
    public void testErrorRecovery() throws Exception {
        setupGameInProgress();
        
        // Test that game can recover from errors without corruption
        int validMoveCount = 0;
        int errorCount = 0;
        
        // Try many operations, some valid, some invalid
        for (int i = 0; i < 100; i++) {
            try {
                if (game.getCurPlayerAction() == 2.0) {
                    // Try to move
                    if (game.getCurPlayer() == 0) {
                        game.moveWorkerAuto(1, 1, 1, 0);
                    } else {
                        game.moveWorkerAuto(3, 3, 3, 2);
                    }
                    validMoveCount++;
                } else if (game.getCurPlayerAction() == 5.0) {
                    // Try to build
                    if (game.getCurPlayer() == 0) {
                        game.buildAuto(1, 0, 0, 0, false);
                    } else {
                        game.buildAuto(3, 2, 2, 2, false);
                    }
                    validMoveCount++;
                }
            } catch (Exception e) {
                errorCount++;
                // Game should still be in valid state after error
                assertTrue(game.getGameStatus() >= -1 && game.getGameStatus() <= 2);
                assertTrue(game.getCurPlayer() >= 0 && game.getCurPlayer() <= 1);
            }
            
            if (game.getGameStatus() == 2) break; // Game ended
        }
        
        // Should have handled both valid operations and errors
        assertTrue("Should have some valid moves", validMoveCount > 0);
        // Game should still be in valid state
        assertTrue(game.getGameStatus() >= -1 && game.getGameStatus() <= 2);
    }

    // ========== Stress Tests ==========
    
    @Test
    public void testLongGameSession() throws Exception {
        // Test that game can handle extended play sessions
        
        for (int gameNum = 0; gameNum < 10; gameNum++) {
            // Create new game
            Player pA = new Player("PlayerA" + gameNum);
            Player pB = new Player("PlayerB" + gameNum);
            Board b = new Board();
            Game g = new Game(b, pA, pB);
            
            // Play complete game
            g.placeWorkerAuto(0, 0);
            g.placeWorkerAuto(0, 1);
            g.placeWorkerAuto(4, 4);
            g.placeWorkerAuto(4, 3);
            
            // Play several turns
            for (int turn = 0; turn < 10 && g.getGameStatus() == 1; turn++) {
                try {
                    // Player A
                    g.moveWorkerAuto(0, 0, 1, 0);
                    g.buildAuto(1, 0, 1, 1, false);
                    
                    if (g.getGameStatus() != 1) break;
                    
                    // Player B
                    g.moveWorkerAuto(4, 4, 3, 4);
                    g.buildAuto(3, 4, 3, 3, false);
                    
                    // Reset positions
                    if (g.getGameStatus() == 1) {
                        g.moveWorkerAuto(1, 0, 0, 0);
                        g.buildAuto(0, 0, 0, 2, false);
                        g.moveWorkerAuto(3, 4, 4, 4);
                        g.buildAuto(4, 4, 4, 2, false);
                    }
                } catch (Exception e) {
                    // Some moves might fail due to board state, that's ok
                    break;
                }
            }
            
            // Game should still be in valid state
            assertTrue(g.getGameStatus() >= -1 && g.getGameStatus() <= 2);
        }
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
}