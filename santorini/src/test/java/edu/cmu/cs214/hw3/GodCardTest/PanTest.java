package edu.cmu.cs214.hw3.GodCardTest;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
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
        Board board = new Board();

        playerA.setGodCard(GodCard.Pan);
        playerB.setGodCard(GodCard.Demeter);

        game = new Game(board, playerA, playerB);

        int aWorkerAR = 0;
        int aWorkerAC = 0;

        int aWorkerBR = 0;
        int aWorkerBC = 1;

        game.placeWorkerAuto(aWorkerAR, aWorkerAC);
        game.placeWorkerAuto(aWorkerBR, aWorkerBC);

        int bWorkerAR = 1;
        int bWorkerAC = 1;

        int bWorkerBR = 2;
        int bWorkerBC = 3;

        game.placeWorkerAuto(bWorkerAR, bWorkerAC);
        game.placeWorkerAuto(bWorkerBR, bWorkerBC);

        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR, aWorkerBC).getWorker().getPlayerName());

        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerBR, bWorkerBC).getWorker().getPlayerName());

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }


    @Test
    public void panWinTest() throws Exception {
        game.getBoard().getCell(1, 0).getTower().build(false);
        assertEquals(1, game.getBoard().getTowerLevel(1,0));

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        game.moveWorkerAuto(0, 0, 1, 0);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        game.moveWorkerAuto(1, 0, 0, 0);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        game.moveWorkerAuto(0, 0, 1, 0);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        game.moveWorkerAuto(1, 0, 0, 0);
        try{
            game.skipActionAdmin();
        } catch (Exception e){
            assertEquals(e.getMessage(), "game has ended");
        }

//        game.skipAction();
//        game.skipAction();

        assertEquals(2, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());

        try{
            game.moveWorkerAuto(0, 0, 1, 0);
        } catch (Exception e){
            assertEquals(e.getMessage(), "game has ended");
        }
    }
}
