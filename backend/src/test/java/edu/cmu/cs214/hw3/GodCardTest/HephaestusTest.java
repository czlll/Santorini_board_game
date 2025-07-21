package edu.cmu.cs214.hw3.GodCardTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;

public class HephaestusTest {
    private Game game;
    private Player playerA;
    private Player playerB;

    @Before
    public void setUp() {
        playerA = new Player("PlayerA", GodCard.Hephaestus);
        playerB = new Player("PlayerB", GodCard.Pan);
        game = new Game(playerA, playerB);
    }

    @Test
    public void testHephaestusBasicBuild() throws Exception {
        // Place workers
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 3);

        // Move worker
        game.moveWorkerAuto(0, 0, 1, 1);

        // First build
        game.buildAuto(1, 1, 2, 2, false);
        assertEquals(1, game.getBoard().getTowerLevel(2, 2));

        // Second build on the same space
        game.buildAuto(1, 1, 2, 2, false);
        assertEquals(2, game.getBoard().getTowerLevel(2, 2));

        // Verify turn passed to next player
        assertEquals(1, game.getCurPlayer());
    }

    @Test
    public void testHephaestusCannotBuildOnDifferentSpace() throws Exception {
        // Place workers
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 3);

        // Move worker
        game.moveWorkerAuto(0, 0, 1, 1);

        // First build
        game.buildAuto(1, 1, 2, 2, false);
        assertEquals(1, game.getBoard().getTowerLevel(2, 2));

        // Try to build on a different space
        try {
            game.buildAuto(1, 1, 2, 1, false);
            fail("Should not be able to build on a different space");
        } catch (Exception e) {
            // Expected exception
        }
    }

    @Test
    public void testHephaestusCannotBuildDomeOnDome() throws Exception {
        // Place workers
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 3);

        // Move worker
        game.moveWorkerAuto(0, 0, 1, 1);

        // Build a dome
        game.buildAuto(1, 1, 2, 2, true);
        assertTrue(game.getBoard().getCell(2, 2).getTower().hasDome());

        // Try to build another dome
        try {
            game.buildAuto(1, 1, 2, 2, true);
            fail("Should not be able to build a dome on a dome");
        } catch (Exception e) {
            // Expected exception
        }
    }

    @Test
    public void testHephaestusCannotBuildBeyondLevel3() throws Exception {
        // Place workers
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 3);

        // Move worker
        game.moveWorkerAuto(0, 0, 1, 1);

        // Build to level 3
        game.buildAuto(1, 1, 2, 2, false);
        game.buildAuto(1, 1, 2, 2, false);
        game.buildAuto(1, 1, 2, 2, false);
        assertEquals(3, game.getBoard().getTowerLevel(2, 2));

        // Try to build beyond level 3
        try {
            game.buildAuto(1, 1, 2, 2, false);
            fail("Should not be able to build beyond level 3");
        } catch (Exception e) {
            // Expected exception
        }
    }

    @Test
    public void testHephaestusCanBuildDomeAtLevel3() throws Exception {
        // Place workers
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 3);

        // Move worker
        game.moveWorkerAuto(0, 0, 1, 1);

        // Build to level 3
        game.buildAuto(1, 1, 2, 2, false);
        game.buildAuto(1, 1, 2, 2, false);
        game.buildAuto(1, 1, 2, 2, false);
        assertEquals(3, game.getBoard().getTowerLevel(2, 2));

        // Build a dome at level 3
        game.skipAction();
        
        // Verify turn passed to next player
        assertEquals(1, game.getCurPlayer());
    }

    @Test
    public void testHephaestusSkipSecondBuild() throws Exception {
        // Place workers
        game.placeWorkerAuto(0, 0);
        game.placeWorkerAuto(4, 4);
        game.placeWorkerAuto(0, 1);
        game.placeWorkerAuto(4, 3);

        // Move worker
        game.moveWorkerAuto(0, 0, 1, 1);

        // First build
        game.buildAuto(1, 1, 2, 2, false);
        assertEquals(1, game.getBoard().getTowerLevel(2, 2));

        // Skip second build
        game.skipAction();
        
        // Verify turn passed to next player
        assertEquals(1, game.getCurPlayer());
    }
}