package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UndoTest {
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
    public void undoTest() throws Exception {
        game.moveWorkerAuto(0, 0, 0, 1);

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());
        assertEquals(5, game.getCurPlayerAction(), 0);

        game.undo();

        assertEquals(game.getPlayerA().getName(), game.getBoard().getCellWorkerName(0, 0));

        assertEquals(1, game.getGameStatus());
        assertEquals(0, game.getCurPlayer());

    }
}
