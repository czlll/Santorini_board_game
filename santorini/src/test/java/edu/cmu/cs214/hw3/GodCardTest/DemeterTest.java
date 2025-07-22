package edu.cmu.cs214.hw3.GodCardTest;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
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
        Board board = new Board();

        playerA.setGodCard(GodCard.Demeter);
        playerB.setGodCard(GodCard.Pan);

        game = new Game(board, playerA, playerB);

        int aWorkerAR = 0;
        int aWorkerAC = 0;

        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorkerAuto(aWorkerAR, aWorkerAC);
        game.placeWorkerAuto(aWorkerBR, aWorkerBC);

        int bWorkerAR = 2;
        int bWorkerAC = 2;

        int bWorkerBR = 3;
        int bWorkerBC = 3;

        game.placeWorkerAuto(bWorkerAR, bWorkerAC);
        game.placeWorkerAuto(bWorkerBR, bWorkerBC);

        assertEquals(playerA.getName(), game.getBoard().getCell(0, 0).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(1, 1).getWorker().getPlayerName());

        assertEquals(playerB.getName(), game.getBoard().getCell(2, 2).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(3, 3).getWorker().getPlayerName());

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }

    @Test
    public void demeterBuildTesting() throws Exception {
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        game.buildAuto(0,0, 0, 1, false);
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5.5, game.getCurPlayerAction(), 0);

        game.buildAuto(0,0, 1, 0, false);
        assertEquals(1, game.getBoard().getTowerLevel(1, 0));

        assertEquals(1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }


    @Test
    public void demeterBuildExceptionTesting() throws Exception {

        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        game.buildAuto(0,0, 0, 1, false);
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5.5, game.getCurPlayerAction(), 0);

        try{
            game.buildAuto(0,0, 0, 1, false);
            fail("test demeterBuildExceptionTesting will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot build on previously built grid");
        }
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5.5, game.getCurPlayerAction(), 0);
    }

    @Test
    public void demeterSkipTesting() throws Exception {

        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

//        if you skip the initial build, you will skip the second option build
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        setGame();

        game.skipActionAdmin();

        game.buildAuto(0,0, 0, 1, false);
        assertEquals(1, game.getBoard().getTowerLevel(0, 1));
//
        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5.5, game.getCurPlayerAction(), 0);


//      you will have the option to skip second build
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);
    }



}