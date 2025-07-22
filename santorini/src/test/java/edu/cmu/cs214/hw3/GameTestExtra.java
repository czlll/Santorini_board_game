package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GameTestExtra {
    private Game game;
    private Player playerA;
    private Player playerB;

    @Before
    public void setGame() throws Exception {
        playerA = new Player("A");
        playerB = new Player("B");
        Board board = new Board();
        playerA.setGodCard(GodCard.Demeter);
        playerB.setGodCard(GodCard.Pan);
        game = new Game(board, playerA, playerB);
    }

    @Test
    public void placeWorkerAutoTest() throws Exception {
        int aWorkerAR = 0;
        int aWorkerAC = 0;

//        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorkerAuto(aWorkerAR, aWorkerAC);

        try{
            game.placeWorkerAuto(aWorkerAR, aWorkerAC);
            fail("test placeWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot place worker because there is already another worker");
        }



        game.placeWorkerAuto(aWorkerBR, aWorkerBC);

        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR, aWorkerBC).getWorker().getPlayerName());


        // test placing worker for playerB
        int bWorkerAR = 2;
        int bWorkerAC = 2;

        int bWorkerBR = 3;
        int bWorkerBC = 3;

        game.placeWorkerAuto(bWorkerAR, bWorkerAC);
        game.placeWorkerAuto(bWorkerBR, bWorkerBC);
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerBR, bWorkerBC).getWorker().getPlayerName());
    }


    /**
     * test for game state update
     * @throws Exception
     */
    @Test
    public void stateTest() throws Exception {
        int aWorkerAR = 0;
        int aWorkerAC = 0;

//        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        assertEquals(-1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(0, game.getCurPlayerAction(), 0);

        game.placeWorkerAuto(aWorkerAR, aWorkerAC);

        assertEquals(-1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(1, game.getCurPlayerAction(), 0);

        game.placeWorkerAuto(aWorkerBR, aWorkerBC);

        assertEquals(-1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(0, game.getCurPlayerAction(), 0);

        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR, aWorkerBC).getWorker().getPlayerName());

        // test placing worker for playerB
        int bWorkerAR = 2;
        int bWorkerAC = 2;

        int bWorkerBR = 3;
        int bWorkerBC = 3;

        game.placeWorkerAuto(bWorkerAR, bWorkerAC);

        assertEquals(-1, game.getGameStatus());
        assertEquals(1, game.getCurPlayer());
        assertEquals(1, game.getCurPlayerAction(), 0);

        game.placeWorkerAuto(bWorkerBR, bWorkerBC);

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(2, game.getCurPlayerAction(), 0);

        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerBR, bWorkerBC).getWorker().getPlayerName());
    }
}
