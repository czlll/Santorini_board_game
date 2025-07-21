package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for DemeterCard.
 */
public class DemeterCardTest {
    private DemeterCard demeterCard;
    private Board board;
    private Player player;
    private Worker worker;

    @Before
    public void setUp() {
        demeterCard = new DemeterCard();
        board = new Board();
        player = new Player("TestPlayer");
        player.setGodCard(demeterCard);
        worker = player.getWorker(0);
        worker.setPosition(new Position(2, 2));
        board.setOccupied(new Position(2, 2), true);
    }

    @Test
    public void testGetName() {
        assertEquals("Demeter", demeterCard.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(demeterCard.getDescription());
        assertTrue(demeterCard.getDescription().contains("additional time"));
    }

    @Test
    public void testOnAfterBuildBlock() {
        Position buildPos = new Position(2, 3);
        GodCard.BuildResult result = demeterCard.onAfterBuild(board, worker, buildPos, false);
        assertEquals(GodCard.BuildResult.ADDITIONAL_BUILD_DIFF, result);
    }

    @Test
    public void testOnAfterBuildDome() {
        Position buildPos = new Position(2, 3);
        GodCard.BuildResult result = demeterCard.onAfterBuild(board, worker, buildPos, true);
        assertEquals(GodCard.BuildResult.NONE, result);
    }

    @Test
    public void testCanBuildNormalRules() {
        Position buildPos = new Position(2, 3);
        assertTrue(demeterCard.canBuild(board, worker, buildPos, false));
    }
}