package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for MinotaurCard.
 */
public class MinotaurCardTest {
    private MinotaurCard minotaurCard;
    private Board board;
    private Player player1;
    private Player player2;
    private Worker worker1;
    private Worker worker2;

    @Before
    public void setUp() {
        minotaurCard = new MinotaurCard();
        board = new Board();
        player1 = new Player("Player1");
        player2 = new Player("Player2");
        player1.setGodCard(minotaurCard);
        
        worker1 = player1.getWorker(0);
        worker2 = player2.getWorker(0);
        
        // Set up initial positions
        worker1.setPosition(new Position(2, 2));
        worker2.setPosition(new Position(2, 3));
        board.setOccupied(new Position(2, 2), true);
        board.setOccupied(new Position(2, 3), true);
    }

    @Test
    public void testGetName() {
        assertEquals("Minotaur", minotaurCard.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(minotaurCard.getDescription());
        assertTrue(minotaurCard.getDescription().contains("opponent Worker"));
    }

    @Test
    public void testCanMoveNormalMove() {
        Position target = new Position(1, 2); // Empty adjacent position
        assertTrue(minotaurCard.canMove(board, worker1, target));
    }

    @Test
    public void testCanMovePushOpponent() {
        Position target = new Position(2, 3); // Position with opponent worker
        Position pushPos = new Position(2, 4); // Where opponent would be pushed
        
        // Ensure push position is empty
        assertFalse(board.isOccupied(pushPos));
        assertFalse(board.hasDome(pushPos));
        
        assertTrue(minotaurCard.canMove(board, worker1, target));
    }

    @Test
    public void testCannotMovePushOpponentBlocked() {
        Position target = new Position(2, 3); // Position with opponent worker
        Position pushPos = new Position(2, 4); // Where opponent would be pushed
        
        // Block the push position
        board.setOccupied(pushPos, true);
        
        assertFalse(minotaurCard.canMove(board, worker1, target));
    }

    @Test
    public void testCannotMovePushOpponentOutOfBounds() {
        // Move worker1 to edge position
        worker1.setPosition(new Position(2, 4));
        board.setOccupied(new Position(2, 2), false);
        board.setOccupied(new Position(2, 4), true);
        
        // Move worker2 to position that would be pushed out of bounds
        worker2.setPosition(new Position(3, 4));
        board.setOccupied(new Position(2, 3), false);
        board.setOccupied(new Position(3, 4), true);
        
        Position target = new Position(3, 4); // Position with opponent worker
        // Push position would be (4, 4) which is out of bounds
        
        assertFalse(minotaurCard.canMove(board, worker1, target));
    }

    @Test
    public void testCannotMovePushOpponentWithDome() {
        Position target = new Position(2, 3); // Position with opponent worker
        Position pushPos = new Position(2, 4); // Where opponent would be pushed
        
        // Place a dome at push position
        board.buildBlock(pushPos);
        board.buildBlock(pushPos);
        board.buildBlock(pushPos);
        board.buildDome(pushPos);
        
        assertFalse(minotaurCard.canMove(board, worker1, target));
    }

    @Test
    public void testCannotMovePushTooHigh() {
        // Build up the target position so height difference > 1
        Position target = new Position(2, 3);
        board.buildBlock(target);
        board.buildBlock(target);
        
        assertFalse(minotaurCard.canMove(board, worker1, target));
    }
}