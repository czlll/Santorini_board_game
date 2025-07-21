package edu.cmu.cs214.santorini.godcards;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.Worker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for HephaestusCard.
 */
public class HephaestusCardTest {
    private HephaestusCard hephaestusCard;
    private Board board;
    private Player player;
    private Worker worker;

    @Before
    public void setUp() {
        hephaestusCard = new HephaestusCard();
        board = new Board();
        player = new Player("TestPlayer");
        player.setGodCard(hephaestusCard);
        worker = player.getWorker(0);
        worker.setPosition(new Position(2, 2));
        board.setOccupied(new Position(2, 2), true);
    }

    @Test
    public void testGetName() {
        assertEquals("Hephaestus", hephaestusCard.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(hephaestusCard.getDescription());
        assertTrue(hephaestusCard.getDescription().contains("additional block"));
    }

    @Test
    public void testOnAfterBuildBlockLevel0() {
        Position buildPos = new Position(2, 3);
        // Build first block
        board.buildBlock(buildPos);
        
        GodCard.BuildResult result = hephaestusCard.onAfterBuild(board, worker, buildPos, false);
        assertEquals(GodCard.BuildResult.ADDITIONAL_BUILD_SAME, result);
    }

    @Test
    public void testOnAfterBuildBlockLevel2() {
        Position buildPos = new Position(2, 3);
        // Build to level 2
        board.buildBlock(buildPos);
        board.buildBlock(buildPos);
        
        GodCard.BuildResult result = hephaestusCard.onAfterBuild(board, worker, buildPos, false);
        assertEquals(GodCard.BuildResult.ADDITIONAL_BUILD_SAME, result);
    }

    @Test
    public void testOnAfterBuildBlockLevel3() {
        Position buildPos = new Position(2, 3);
        // Build to level 3
        board.buildBlock(buildPos);
        board.buildBlock(buildPos);
        board.buildBlock(buildPos);
        
        GodCard.BuildResult result = hephaestusCard.onAfterBuild(board, worker, buildPos, false);
        assertEquals(GodCard.BuildResult.NONE, result);
    }

    @Test
    public void testOnAfterBuildDome() {
        Position buildPos = new Position(2, 3);
        GodCard.BuildResult result = hephaestusCard.onAfterBuild(board, worker, buildPos, true);
        assertEquals(GodCard.BuildResult.NONE, result);
    }
}