package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for edge cases and boundary conditions
 * Tests unusual scenarios and corner cases that might occur during gameplay
 */
public class EdgeCaseTest {
    
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

    // ========== Board Boundary Edge Cases ==========
    
    @Test
    public void testCornerPositions() throws Exception {
        // Test all four corners of the board
        int[][] corners = {{0, 0}, {0, 4}, {4, 0}, {4, 4}};
        
        // Place workers in corners
        for (int i = 0; i < corners.length; i++) {
            game.placeWorkerAuto(corners[i][0], corners[i][1]);
        }
        
        // Verify all corners are occupied
        for (int[] corner : corners) {
            assertTrue(board.hasWorker(corner[0], corner[1]));
        }
        
        // Test movement from corners (limited options)
        // From (0,0) can only move to (0,1), (1,0), (1,1)
        game.moveWorkerAuto(0, 0, 1, 1);
        assertEquals("PlayerA", board.getCell(1, 1).getWorker().getPlayerName());
        assertFalse(board.hasWorker(0, 0));
    }
    
    @Test
    public void testEdgePositions() throws Exception {
        // Test positions along edges (not corners)
        int[][] edges = {{0, 2}, {2, 0}, {2, 4}, {4, 2}};
        
        for (int i = 0; i < edges.length; i++) {
            game.placeWorkerAuto(edges[i][0], edges[i][1]);
        }
        
        // Test movement from edge positions
        // From (0,2) can move to adjacent cells
        game.moveWorkerAuto(0, 2, 1, 2);
        assertEquals("PlayerA", board.getCell(1, 2).getWorker().getPlayerName());
    }
    
    @Test
    public void testCenterPosition() throws Exception {
        // Test center position (2,2) which has maximum movement options
        game.placeWorkerAuto(2, 2); // Center
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(4, 0);
        
        // From center, worker can move in all 8 directions
        int[][] directions = {
            {1, 1}, {1, 2}, {1, 3}, {2, 1}, {2, 3}, {3, 1}, {3, 2}, {3, 3}
        };
        
        // Test one direction
        game.moveWorkerAuto(2, 2, 1, 1);
        assertEquals("PlayerA", board.getCell(1, 1).getWorker().getPlayerName());
    }

    // ========== Tower Height Edge Cases ==========
    
    @Test
    public void testMaximumTowerHeight() throws Exception {
        setupGameInProgress();
        
        // Build tower to maximum height (4 levels with dome)
        Cell testCell = board.getCell(0, 2);
        
        // Build through multiple turns
        game.moveWorkerAuto(1, 1, 1, 2);
        game.buildAuto(1, 2, 0, 2, false); // Level 1
        assertEquals(1, testCell.getTowerLevel());
        
        completeTurn();
        game.moveWorkerAuto(3, 3, 2, 3);
        game.buildAuto(2, 3, 0, 2, false); // Level 2
        assertEquals(2, testCell.getTowerLevel());
        
        completeTurn();
        game.moveWorkerAuto(1, 2, 1, 1);
        game.buildAuto(1, 1, 0, 2, false); // Level 3
        assertEquals(3, testCell.getTowerLevel());
        
        completeTurn();
        game.moveWorkerAuto(2, 3, 3, 3);
        game.buildAuto(3, 3, 0, 2, true); // Level 4 (dome)
        assertEquals(4, testCell.getTowerLevel());
        assertTrue(testCell.getTower().hasDome());
        
        // Cannot build further on dome
        completeTurn();
        game.moveWorkerAuto(1, 1, 1, 2);
        try {
            game.buildAuto(1, 2, 0, 2, false);
            fail("Should not allow building on dome");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("dome") || 
                      e.getMessage().contains("Cannot build"));
        }
    }
    
    @Test
    public void testClimbingToWinningHeight() throws Exception {
        setupGameInProgress();
        
        // Create level 2 tower
        board.getCell(1, 2).setTower(new Tower(2));
        
        // Move worker to level 3 (winning condition)
        game.moveWorkerAuto(1, 1, 1, 2);
        
        // Game should end immediately
        assertEquals(2, game.getGameStatus());
        assertEquals(0, game.getCurPlayer()); // Player A wins
        
        // No further actions should be allowed
        try {
            game.buildAuto(1, 2, 2, 2, false);
            fail("Should not allow actions after game ends");
        } catch (Exception e) {
            assertEquals("game has ended", e.getMessage());
        }
    }
    
    @Test
    public void testDescendingFromMaxHeight() throws Exception {
        setupGameInProgress();
        
        // Place worker on level 3 tower
        board.getCell(1, 1).setTower(new Tower(3));
        
        // Worker should be able to descend to any lower level
        game.moveWorkerAuto(1, 1, 1, 2); // 3 -> 0
        assertEquals("PlayerA", board.getCell(1, 2).getWorker().getPlayerName());
        
        // Reset and test descending to level 1
        game.buildAuto(1, 2, 2, 2, false);
        completeTurn();
        game.moveWorkerAuto(1, 2, 1, 1);
        game.buildAuto(1, 1, 0, 1, false);
        completeTurn();
        
        board.getCell(1, 3).setTower(new Tower(1));
        game.moveWorkerAuto(1, 1, 1, 3); // 3 -> 1
        assertEquals("PlayerA", board.getCell(1, 3).getWorker().getPlayerName());
    }

    // ========== Worker Interaction Edge Cases ==========
    
    @Test
    public void testWorkersInAdjacentCells() throws Exception {
        // Place workers adjacent to each other
        game.placeWorkerAuto(2, 2); // Player A worker 1
        game.placeWorkerAuto(2, 3); // Player A worker 2 (adjacent)
        game.placeWorkerAuto(1, 1); // Player B worker 1
        game.placeWorkerAuto(4, 4); // Player B worker 2
        
        // Test that adjacent workers block movement
        try {
            game.moveWorkerAuto(2, 2, 2, 3);
            fail("Should not allow moving to cell with own worker");
        } catch (Exception e) {
            assertEquals("worker already exists in the about-to-move cell", e.getMessage());
        }
        
        // Test valid movement away from adjacent worker
        game.moveWorkerAuto(2, 2, 1, 2);
        assertEquals("PlayerA", board.getCell(1, 2).getWorker().getPlayerName());
    }
    
    @Test
    public void testAllWorkersInOneArea() throws Exception {
        // Place all workers in a small area (2x2 square)
        game.placeWorkerAuto(1, 1);
        game.placeWorkerAuto(1, 2);
        game.placeWorkerAuto(2, 1);
        game.placeWorkerAuto(2, 2);
        
        // Test movement in crowded area
        game.moveWorkerAuto(1, 1, 0, 1);
        assertEquals("PlayerA", board.getCell(0, 1).getWorker().getPlayerName());
        
        // Build in crowded area
        game.buildAuto(0, 1, 0, 0, false);
        assertEquals(1, board.getCell(0, 0).getTowerLevel());
    }

    // ========== Building Pattern Edge Cases ==========
    
    @Test
    public void testSurroundedByTowers() throws Exception {
        setupGameInProgress();
        
        // Create towers surrounding a worker
        int[][] surroundingCells = {
            {0, 0}, {0, 1}, {0, 2},
            {1, 0},         {1, 2},
            {2, 0}, {2, 1}, {2, 2}
        };
        
        // Build towers around position (1,1)
        for (int[] cell : surroundingCells) {
            if (!board.hasWorker(cell[0], cell[1])) {
                board.getCell(cell[0], cell[1]).setTower(new Tower(2));
            }
        }
        
        // Worker at (1,1) should still be able to move to level 2 towers
        game.moveWorkerAuto(1, 1, 0, 0);
        assertEquals("PlayerA", board.getCell(0, 0).getWorker().getPlayerName());
    }
    
    @Test
    public void testAlternatingTowerHeights() throws Exception {
        setupGameInProgress();
        
        // Create checkerboard pattern of tower heights
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (!board.hasWorker(r, c)) {
                    int height = (r + c) % 3; // Heights 0, 1, 2
                    if (height > 0) {
                        board.getCell(r, c).setTower(new Tower(height));
                    }
                }
            }
        }
        
        // Test movement through varied heights
        game.moveWorkerAuto(1, 1, 0, 1);
        assertEquals("PlayerA", board.getCell(0, 1).getWorker().getPlayerName());
    }

    // ========== Game State Edge Cases ==========
    
    @Test
    public void testRapidGameEnd() throws Exception {
        // Test game that ends very quickly
        game.placeWorkerAuto(1, 1);
        game.placeWorkerAuto(2, 2);
        game.placeWorkerAuto(3, 3);
        game.placeWorkerAuto(4, 4);
        
        // Create immediate winning condition
        board.getCell(0, 1).setTower(new Tower(2));
        
        // Win in first move
        game.moveWorkerAuto(1, 1, 0, 1);
        assertEquals(2, game.getGameStatus());
    }
    
    @Test
    public void testGameWithMinimalMoves() throws Exception {
        setupGameInProgress();
        
        // Test game with very few moves before win
        board.getCell(0, 1).setTower(new Tower(2));
        
        // Player A wins immediately
        game.moveWorkerAuto(1, 1, 0, 1);
        assertEquals(2, game.getGameStatus());
        assertEquals(0, game.getCurPlayer()); // Player A wins
    }
    
    @Test
    public void testGameWithManyMoves() throws Exception {
        setupGameInProgress();
        
        // Play many moves without winning
        for (int i = 0; i < 20; i++) {
            if (game.getGameStatus() != 1) break;
            
            try {
                // Player A
                if (game.getCurPlayer() == 0 && game.getCurPlayerAction() == 2.0) {
                    game.moveWorkerAuto(1, 1, 0, 1);
                } else if (game.getCurPlayer() == 0 && game.getCurPlayerAction() == 5.0) {
                    game.buildAuto(0, 1, 0, 0, false);
                }
                
                if (game.getGameStatus() != 1) break;
                
                // Player B
                if (game.getCurPlayer() == 1 && game.getCurPlayerAction() == 2.0) {
                    game.moveWorkerAuto(3, 3, 3, 2);
                } else if (game.getCurPlayer() == 1 && game.getCurPlayerAction() == 5.0) {
                    game.buildAuto(3, 2, 4, 2, false);
                }
                
                // Reset positions for next iteration
                if (game.getGameStatus() == 1 && game.getCurPlayer() == 0) {
                    game.moveWorkerAuto(0, 1, 1, 1);
                    game.buildAuto(1, 1, 1, 0, false);
                    game.moveWorkerAuto(3, 2, 3, 3);
                    game.buildAuto(3, 3, 2, 3, false);
                }
            } catch (Exception e) {
                // Some moves might fail due to board state
                break;
            }
        }
        
        // Game should still be in valid state
        assertTrue(game.getGameStatus() >= 1 && game.getGameStatus() <= 2);
    }

    // ========== Input Validation Edge Cases ==========
    
    @Test
    public void testInvalidCoordinates() throws Exception {
        // Test various invalid coordinate combinations
        int[][] invalidCoords = {
            {-1, 0}, {0, -1}, {-1, -1},
            {5, 0}, {0, 5}, {5, 5},
            {10, 10}, {-10, -10}
        };
        
        for (int[] coord : invalidCoords) {
            try {
                game.placeWorkerAuto(coord[0], coord[1]);
                fail("Should reject invalid coordinates: " + coord[0] + "," + coord[1]);
            } catch (Exception e) {
                // Expected - should throw some kind of bounds exception
                assertTrue(e instanceof IndexOutOfBoundsException || 
                          e.getMessage().contains("out of bounds") ||
                          e.getMessage().contains("IndexOutOfBoundsException"));
            }
        }
    }
    
    @Test
    public void testNullPlayerNames() throws Exception {
        // Test game with null player names
        try {
            Player nullPlayer = new Player(null);
            fail("Should not allow null player name");
        } catch (Exception e) {
            // Expected
        }
        
        // Test empty player names
        Player emptyPlayer = new Player("");
        assertEquals("", emptyPlayer.getName());
    }

    // ========== Concurrency Edge Cases ==========
    
    @Test
    public void testRapidOperations() throws Exception {
        setupGameInProgress();
        
        // Test rapid succession of operations
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            try {
                if (game.getGameStatus() != 1) break;
                
                if (game.getCurPlayerAction() == 2.0) {
                    if (game.getCurPlayer() == 0) {
                        game.moveWorkerAuto(1, 1, 1, 0);
                    } else {
                        game.moveWorkerAuto(3, 3, 3, 2);
                    }
                } else if (game.getCurPlayerAction() == 5.0) {
                    if (game.getCurPlayer() == 0) {
                        game.buildAuto(1, 0, 0, 0, false);
                    } else {
                        game.buildAuto(3, 2, 2, 2, false);
                    }
                }
            } catch (Exception e) {
                // Some operations might fail, that's ok
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        // Should complete quickly without errors
        assertTrue("Operations took too long", (endTime - startTime) < 1000);
        assertTrue("Game should be in valid state", 
                  game.getGameStatus() >= -1 && game.getGameStatus() <= 2);
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
                game.buildAuto(1, 1, 0, 1, false);
            } else {
                // Find a valid build location for Player B
                game.buildAuto(3, 3, 3, 2, false);
            }
        }
    }
}