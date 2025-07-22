package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {

    private Game game;
    private Player playerA;
    private Player playerB;

    @Before
    public void setGame() throws Exception {
        playerA = new Player("A");
        playerB = new Player("B");
        Board board = new Board();
        game = new Game(board, playerA, playerB);
    }

    @Test
    public void integrationTest() throws Exception {
//        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

//        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        // graph indicating the location of the workers
        // awa, means playerA's worker A
        // awb, means playerA's worker B
        // first letter indicate player, middle letter indicate worker, last letter indicate workerA or workerB
//        +---+---+---+---+---+
//        |awa|   |   |   |   |
//        +---+---+---+---+---+
//        |   |awb|   |   |   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+

        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);
//        assertEquals(playerA.getName(), aWorkerA.getPlayerName());
//        assertEquals(playerA.getName(), aWorkerB.getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR, aWorkerBC).getWorker().getPlayerName());


        // test placing worker for playerB
//        Worker bWorkerA = new Worker();
        int bWorkerAR = 2;
        int bWorkerAC = 2;

//        Worker bWorkerB = new Worker();
        int bWorkerBR = 3;
        int bWorkerBC = 3;

//        +---+---+---+---+---+
//        |awa|   |   |   |   |
//        +---+---+---+---+---+
//        |   |awb|   |   |   |
//        +---+---+---+---+---+
//        |   |   |bwa|   |   |
//        +---+---+---+---+---+
//        |   |   |   |bwb|   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+

        game.placeWorker(1, 0, bWorkerAR, bWorkerAC);
        game.placeWorker(1, 1, bWorkerBR, bWorkerBC);
//        assertEquals(playerB.getName(), bWorkerA.getPlayerName());
//        assertEquals(playerB.getName(), bWorkerB.getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerBR, bWorkerBC).getWorker().getPlayerName());


        // after the following two moves
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
//        |awa|awb|   |   |   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
//        |   |   |bwa|bwb|   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
        game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A");
        aWorkerAR++;
        game.moveWorker(bWorkerAR, bWorkerAC, bWorkerAR+1, bWorkerAC, "B");
        bWorkerAR++;
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());


        // after the following two moves
        // T1 means tower with level 1
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
//        |awa|awb|   |   |   |
//        +---+---+---+---+---+
//        | T1|   |bwa|   |   |
//        +---+---+---+---+---+
//        |   |   |   |bwb|   |
//        +---+---+---+---+---+
//        |   |   |   |   |   |
//        +---+---+---+---+---+
        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", false);
        game.moveWorker(bWorkerAR, bWorkerAC, bWorkerAR-1, bWorkerAC, "B");
        bWorkerAR--;
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAR).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(1, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());


        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |      |awb|   |   |   |
//        +------+---+---+---+---+
//        |T1 awa|   |   |bwa|   |
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A");
        aWorkerAR++;
        game.moveWorker(bWorkerAR, bWorkerAC, bWorkerAR, bWorkerAC+1, "B");
        bWorkerAC++;
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());


        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |  T1  |awb|   |   |   |
//        +------+---+---+---+---+
//        |T1 awa|   |   |   |bwa|
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.build(aWorkerAR, aWorkerAC, aWorkerAR-1, aWorkerAC, "A", false);
        game.moveWorker(bWorkerAR, bWorkerAC, bWorkerAR, bWorkerAC+1, "B");
        bWorkerAC++;
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(1, game.getBoard().getCell(aWorkerAR-1, aWorkerAC).getTowerLevel());

        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |  T2  |awb|   |   |T1 |
//        +------+---+---+---+---+
//        |T1 awa|   |   |   |bwa|
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.build(aWorkerAR, aWorkerAC, aWorkerAR-1, aWorkerAC, "A", false);
        game.build(bWorkerAR, bWorkerAC, bWorkerAR-1, bWorkerAC, "B", false);
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(2, game.getBoard().getCell(aWorkerAR-1, aWorkerAC).getTowerLevel());
        assertEquals(1, game.getBoard().getCell(bWorkerAR-1, bWorkerAC).getTowerLevel());


        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |T2 awa|awb|   |   |T2 |
//        +------+---+---+---+---+
//        |  T1  |   |   |   |bwa|
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR-1, aWorkerAC, "A");
        aWorkerAR--;
        game.build(bWorkerAR, bWorkerAC, bWorkerAR-1, bWorkerAC, "B", false);
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(2, game.getBoard().getCell(aWorkerAR, aWorkerAC).getTowerLevel());
        assertEquals(1, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());
        assertEquals(2, game.getBoard().getCell(bWorkerAR-1, bWorkerAC).getTowerLevel());


        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |T2 awa|awb|   |   |T3 |
//        +------+---+---+---+---+
//        |  T2  |   |   |   |bwa|
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", false);
        game.build(bWorkerAR, bWorkerAC, bWorkerAR-1, bWorkerAC, "B", false);
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(2, game.getBoard().getCell(aWorkerAR, aWorkerAC).getTowerLevel());
        assertEquals(2, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());
        assertEquals(3, game.getBoard().getCell(bWorkerAR-1, bWorkerAC).getTowerLevel());


        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |T2 awa|awb|   |   | D |
//        +------+---+---+---+---+
//        |  T3  |   |   |   |bwa|
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", false);
        game.build(bWorkerAR, bWorkerAC, bWorkerAR-1, bWorkerAC, "B", true);
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(2, game.getBoard().getCell(aWorkerAR, aWorkerAC).getTowerLevel());
        assertEquals(3, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());
        assertEquals(4, game.getBoard().getCell(bWorkerAR-1, bWorkerAC).getTowerLevel());
        assertTrue(game.getBoard().getCell(bWorkerAR-1, bWorkerAC).getTower().hasDome());


        // after the following two moves
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
//        |  T2  |awb|   |   | D |
//        +------+---+---+---+---+
//        |T3 awa|   |   |   |bwa|
//        +------+---+---+---+---+
//        |      |   |   |bwb|   |
//        +------+---+---+---+---+
//        |      |   |   |   |   |
//        +------+---+---+---+---+
        game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A");
        aWorkerAR++;

        try {
            game.moveWorker(bWorkerAR, bWorkerAC, bWorkerAR, bWorkerAC-1, "B");
        } catch (Exception e){
            assertEquals(e.getMessage(), "game has ended");
        }
//        bWorkerAC--;

        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(3, game.getBoard().getCell(aWorkerAR, aWorkerAC).getTowerLevel());
        assertEquals(game.getGameStatus(), 2);
//        assertEquals(0, game.getCurPlayer());


    }
}
