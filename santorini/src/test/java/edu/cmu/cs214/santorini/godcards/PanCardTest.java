package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for PanCard.
 */
public class PanCardTest {
    private PanCard panCard;
    private Board board;
    private Player player;
    private Worker worker;

    @Before
    public void setUp() {
        panCard = new PanCard();
        board = new Board();
        player = new Player("TestPlayer");
        player.setGodCard(panCard);
        worker = player.getWorker(0);
    }

    @Test
    public void testGetName() {
        assertEquals("Pan", panCard.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(panCard.getDescription());
        assertTrue(panCard.getDescription().contains("down two or more levels"));
    }

    @Test
    public void testOnAfterMoveDownTwoLevels() {
        // Set up positions with height difference
        Position from = new Position(2, 2);
        Position to = new Position(2, 3);
        
        // Build up the from position to level 2
        board.buildBlock(from);
        board.buildBlock(from);
        
        // Leave to position at ground level (0)
        
        boolean result = panCard.onAfterMove(board, worker, from, to);
        assertTrue(result); // Should win with 2-level drop
    }

    @Test
    public void testOnAfterMoveDownThreeLevels() {
        Position from = new Position(2, 2);
        Position to = new Position(2, 3);
        
        // Build up the from position to level 3
        board.buildBlock(from);
        board.buildBlock(from);
        board.buildBlock(from);
        
        // Build to position to level 1
        board.buildBlock(to);
        
        boolean result = panCard.onAfterMove(board, worker, from, to);
        assertTrue(result); // Should win with 2-level drop (3->1)
    }

    @Test
    public void testOnAfterMoveDownOneLevel() {
        Position from = new Position(2, 2);
        Position to = new Position(2, 3);
        
        // Build up the from position to level 1
        board.buildBlock(from);
        
        // Leave to position at ground level (0)
        
        boolean result = panCard.onAfterMove(board, worker, from, to);
        assertFalse(result); // Should not win with only 1-level drop
    }

    @Test
    public void testOnAfterMoveUp() {
        Position from = new Position(2, 2);
        Position to = new Position(2, 3);
        
        // Leave from position at ground level (0)
        
        // Build up the to position to level 2
        board.buildBlock(to);
        board.buildBlock(to);
        
        boolean result = panCard.onAfterMove(board, worker, from, to);
        assertFalse(result); // Should not win when moving up
    }
}