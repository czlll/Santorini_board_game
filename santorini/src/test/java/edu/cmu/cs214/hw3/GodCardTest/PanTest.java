package edu.cmu.cs214.hw3.GodCardTest;

import edu.cmu.cs214.santorini.Board;
import edu.cmu.cs214.santorini.Game;
import edu.cmu.cs214.santorini.Position;
import edu.cmu.cs214.santorini.godcards.GodCardRegistry;
import edu.cmu.cs214.santorini.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PanTest {

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
        game.assignGodCard(0, "Pan");
        game.assignGodCard(1, "Demeter");

        // Place workers alternating between players
        game.placeWorker(0, new Position(0, 0)); // Player A worker 0 -> switches to Player B
        game.placeWorker(0, new Position(1, 1)); // Player B worker 0 -> switches to Player A
        game.placeWorker(1, new Position(0, 1)); // Player A worker 1 -> switches to Player B
        game.placeWorker(1, new Position(2, 3)); // Player B worker 1 -> game starts, Player A current

        // Verify worker positions
        assertEquals(new Position(0, 0), playerA.getWorker(0).getPosition());
        assertEquals(new Position(0, 1), playerA.getWorker(1).getPosition());
        assertEquals(new Position(1, 1), playerB.getWorker(0).getPosition());
        assertEquals(new Position(2, 3), playerB.getWorker(1).getPosition());

        // Verify game state transitioned to MOVE after all workers placed
        assertEquals(Game.GameState.MOVE, game.getState());
        assertEquals(playerA, game.getCurrentPlayer());

        assertEquals(1, game.getState().ordinal());
        assertEquals(0, game.getPlayers().indexOf(game.getCurrentPlayer()));
        assertEquals(2, 2, 0);
    }


    @Test
    public void panWinTest() throws Exception {
        game.getBoard().getCell(1, 0).getTower().build(false);
        assertEquals(1, game.getBoard().getTowerLevel(1,0));

        assertEquals(1, game.getState().ordinal());
        assertEquals(0, game.getPlayers().indexOf(game.getCurrentPlayer()));
        assertEquals(2, 2, 0);

        game.moveWorkerAuto(0, 0, 1, 0);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();

        assertEquals(1, game.getState().ordinal());
        assertEquals(0, game.getPlayers().indexOf(game.getCurrentPlayer()));
        assertEquals(2, 2, 0);

        game.moveWorkerAuto(1, 0, 0, 0);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();

        assertEquals(1, game.getState().ordinal());
        assertEquals(0, game.getPlayers().indexOf(game.getCurrentPlayer()));
        assertEquals(2, 2, 0);

        game.moveWorkerAuto(0, 0, 1, 0);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();

        assertEquals(1, game.getState().ordinal());
        assertEquals(0, game.getPlayers().indexOf(game.getCurrentPlayer()));
        assertEquals(2, 2, 0);

        game.moveWorkerAuto(1, 0, 0, 0);
        try{
            game.skipActionAdmin();
        } catch (Exception e){
            assertEquals(e.getMessage(), "game has ended");
        }

//        game.skipAction();
//        game.skipAction();

        assertEquals(2, game.getState().ordinal());
        assertEquals(0, game.getPlayers().indexOf(game.getCurrentPlayer()));

        try{
            game.moveWorkerAuto(0, 0, 1, 0);
        } catch (Exception e){
            assertEquals(e.getMessage(), "game has ended");
        }
    }
}
