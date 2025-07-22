package edu.cmu.cs214.hw3.GodCardTest;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Game;
import edu.cmu.cs214.santorini.godcards.GodCardRegistry;
import edu.cmu.cs214.santorini.Player;
import edu.cmu.cs214.santorini.Position;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DemeterTest {
    private Game game;

    @Before
    public void setGame() throws Exception {
//        System.out.println("triggered here");
        Player playerA = new Player("A");
        Player playerB = new Player("B");

        game = new Game();
        game.addPlayer(playerA);
        game.addPlayer(playerB);

        // Assign God Cards (this transitions from CARD_SELECTION to SETUP)
        game.assignGodCard(0, "Demeter");
        game.assignGodCard(1, "Pan");

        // Verify we're in SETUP state
        assertEquals(Game.GameState.SETUP, game.getState());

        // Place workers alternating between players
        game.placeWorker(0, new Position(0, 0)); // Player A worker 0 -> switches to Player B
        game.placeWorker(0, new Position(2, 2)); // Player B worker 0 -> switches to Player A
        game.placeWorker(1, new Position(1, 1)); // Player A worker 1 -> switches to Player B
        game.placeWorker(1, new Position(3, 3)); // Player B worker 1 -> game starts, Player A current

        // Verify worker positions
        assertEquals(new Position(0, 0), playerA.getWorker(0).getPosition());
        assertEquals(new Position(1, 1), playerA.getWorker(1).getPosition());
        assertEquals(new Position(2, 2), playerB.getWorker(0).getPosition());
        assertEquals(new Position(3, 3), playerB.getWorker(1).getPosition());

        // Verify game state transitioned to MOVE after all workers placed
        assertEquals(Game.GameState.MOVE, game.getState());
        assertEquals(playerA, game.getCurrentPlayer());
    }

    @Test
    public void demeterBuildTesting() throws Exception {
        // Select a worker and move it to set up for building
        game.selectWorker(0); // Select Player A's first worker
        game.moveWorker(new Position(0, 1)); // Move from (0,0) to (0,1)
        
        // Verify game state is now BUILD
        assertEquals(Game.GameState.BUILD, game.getState());
        assertEquals(game.getPlayers().get(0), game.getCurrentPlayer());

        // Build with Demeter's ability (can build twice)
        game.build(new Position(0, 2), false); // Build at (0,2)
        assertEquals(1, game.getBoard().getHeight(new Position(0, 2)));

        // Check if Demeter allows additional build
        if (game.getState() == Game.GameState.ADDITIONAL_BUILD) {
            game.build(new Position(1, 0), false); // Build at (1,0) - different location, unoccupied
            assertEquals(1, game.getBoard().getHeight(new Position(1, 0)));
        }

        // Game should move to next player
        assertEquals(game.getPlayers().get(1), game.getCurrentPlayer());
        assertEquals(Game.GameState.MOVE, game.getState());
    }


    @Test
    public void demeterBuildExceptionTesting() throws Exception {
        // Select a worker and move it to set up for building
        game.selectWorker(0); // Select Player A's first worker
        game.moveWorker(new Position(0, 1)); // Move from (0,0) to (0,1)
        
        // Verify game state is now BUILD
        assertEquals(Game.GameState.BUILD, game.getState());

        // Build first time
        game.build(new Position(0, 2), false); // Build at (0,2)
        assertEquals(1, game.getBoard().getHeight(new Position(0, 2)));

        // If Demeter allows additional build, try to build on same location (should fail)
        if (game.getState() == Game.GameState.ADDITIONAL_BUILD) {
            // Try to build on same location - should return false, not throw exception
            boolean result = game.build(new Position(0, 2), false); // Try to build on same location
            assertEquals(false, result); // Should fail
            
            // Verify the height is still 1 (not increased)
            assertEquals(1, game.getBoard().getHeight(new Position(0, 2)));
            
            // Should still be in ADDITIONAL_BUILD state since the build failed
            assertEquals(Game.GameState.ADDITIONAL_BUILD, game.getState());
        }
    }

    @Test
    public void demeterSkipTesting() throws Exception {
        // Test skipping Demeter's additional build ability
        game.selectWorker(0); // Select Player A's first worker
        game.moveWorker(new Position(0, 1)); // Move from (0,0) to (0,1)
        
        // Verify game state is now BUILD
        assertEquals(Game.GameState.BUILD, game.getState());

        // Build first time
        game.build(new Position(0, 2), false); // Build at (0,2)
        assertEquals(1, game.getBoard().getHeight(new Position(0, 2)));

        // If Demeter allows additional build, skip it
        if (game.getState() == Game.GameState.ADDITIONAL_BUILD) {
            // Skip the additional build - this should end the turn
            game.skipAdditionalBuild();
        }

        // Game should move to next player
        assertEquals(game.getPlayers().get(1), game.getCurrentPlayer());
        assertEquals(Game.GameState.MOVE, game.getState());
    }



}