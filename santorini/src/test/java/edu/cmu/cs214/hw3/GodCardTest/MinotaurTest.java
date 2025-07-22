package edu.cmu.cs214.hw3.GodCardTest;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MinotaurTest {
    private Game game;

    @Before
    public void setGame() throws Exception {
//        System.out.println("triggered here");
        Player playerA = new Player("A");
        Player playerB = new Player("B");
        Board board = new Board();

        playerA.setGodCard(GodCard.Minotaur);
        playerB.setGodCard(GodCard.Pan);

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
    public void minotaurMoveTesting() throws Exception {
//      test for push opponent backward
        game.moveWorkerAuto(0, 0, 1, 1);

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        assertEquals(game.getPlayerA().getName(), game.getBoard().getCell(1, 1).getWorker().getPlayerName());
        assertEquals(game.getPlayerB().getName(), game.getBoard().getCell(2, 2).getWorker().getPlayerName());

        setGame();

//        build a level 2 tower behind opponent worker
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.buildAuto(2,3,2,2, false);

        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.buildAuto(2,3,2,2, false);


        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

//        minotaur can still push opponent to the tower
        game.moveWorkerAuto(0, 0, 1, 1);

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        assertEquals(game.getPlayerA().getName(), game.getBoard().getCell(1, 1).getWorker().getPlayerName());
        assertEquals(game.getPlayerB().getName(), game.getBoard().getCell(2, 2).getWorker().getPlayerName());
    }


    @Test
    public void minotaurBuildExceptionTesting() throws Exception {

        game.moveWorkerAuto(0,0,1,0);
        game.skipActionAdmin();
        game.moveWorkerAuto(1,1,0,0);
        game.skipActionAdmin();
        game.moveWorkerAuto(1,0,1,1);
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.skipActionAdmin();


        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        assertEquals(game.getPlayerA().getName(), game.getBoard().getCell(1, 1).getWorker().getPlayerName());
        assertEquals(game.getPlayerB().getName(), game.getBoard().getCell(0, 0).getWorker().getPlayerName());

        try{
            game.moveWorkerAuto(1,1,0,0);
            fail("test MinotaurBuildExceptionTesting will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Minotaur is unable to push opponent out of gameboard");
        }

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        setGame();
        game.skipActionAdmin();
        game.skipActionAdmin();
        game.moveWorkerAuto(2,3,2,2);
        game.skipActionAdmin();

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        try{
            game.moveWorkerAuto(0,0,1,1);
            fail("test MinotaurBuildExceptionTesting will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Minotaur is unable to push, because opponent worker have friend to back-him-up");
        }
    }
}