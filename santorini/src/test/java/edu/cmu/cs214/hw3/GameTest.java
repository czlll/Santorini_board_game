package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class GameTest
{
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

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testPlaceWorker() throws Exception {
        // test placing worker for playerA
//        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

//        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);
//        System.out.println(playerA.getName());
//        System.out.println(game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR, aWorkerBC).getWorker().getPlayerName());
//        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());


        // test placing worker for playerB
//        Worker bWorkerA = new Worker();
        int bWorkerAR = 2;
        int bWorkerAC = 2;

//        Worker bWorkerB = new Worker();
        int bWorkerBR = 3;
        int bWorkerBC = 3;

        game.placeWorker(1, 0, bWorkerAR, bWorkerAC);
        game.placeWorker(1, 1, bWorkerBR, bWorkerBC);
//        assertEquals(playerB.getName(), bWorkerA.getPlayerName());
//        assertEquals(playerB.getName(), bWorkerB.getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerBR, bWorkerBC).getWorker().getPlayerName());


        // test place worker multiple times
        Worker bWorkerA2 = new Worker();
        int bWorkerAR2 = 4;
        int bWorkerAC2 = 4;

        Worker bWorkerB2 = new Worker();
        int bWorkerBR2 = 1;
        int bWorkerBC2 = 4;

        // set worker for the same player multiple times will remove the previous workers
        game.placeWorker(1, 0, bWorkerAR2, bWorkerAC2);
        game.placeWorker(1, 1, bWorkerBR2, bWorkerBC2);
//        assertEquals(playerB.getName(), bWorkerA2.getPlayerName());
//        assertEquals(playerB.getName(), bWorkerB2.getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerAR2, bWorkerAC2).getWorker().getPlayerName());
        assertEquals(playerB.getName(), game.getBoard().getCell(bWorkerBR2, bWorkerBC2).getWorker().getPlayerName());


        // previous worker name become empty string and will be removed
        // to make this works, we have to ensure that the two request use different worker instance
        assertEquals("", game.getBoard().getCell(bWorkerAR, bWorkerAC).getWorker().getPlayerName());
        assertFalse(game.getBoard().getCell(bWorkerAR, bWorkerAC).hasWorker());

    }

    /**
     * this class will trigger exception because workers were trying to placed at the same cell
     * @throws Exception
     */
    @Test
    public void testPlaceWorkerException() throws Exception {
        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

//        game.placeWorker(1, aWorkerA, aWorkerB, aWorkerAR, aWorkerAC, aWorkerBR, aWorkerBC);
        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);

        // test placing worker on the same location
        Worker bWorkerA = new Worker();
        int bWorkerAR = 0;
        int bWorkerAC = 0;

        Worker bWorkerB = new Worker();
        int bWorkerBR = 1;
        int bWorkerBC = 1;

        try{
            game.placeWorker(1, 0, bWorkerAR, bWorkerAC);
            game.placeWorker(1, 1, bWorkerBR, bWorkerBC);
            fail("test placeWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot place worker because there is already another worker");
        }


    }

    @Test
    public void testMoveWorker() throws Exception {
        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);

        // test move workerA
        game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A");
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getWorker().getPlayerName());
        // test worker no longer in original position after moved
        assertFalse(game.getBoard().getCell(aWorkerAR, aWorkerAC).hasWorker());

        // hard code tower for testing purposes
        game.getBoard().getCell(aWorkerBR, aWorkerBC).setTower(new Tower(1));
        game.getBoard().getCell(aWorkerBR+1, aWorkerBC).setTower(new Tower(2));

        // test moving workerB between towers
        game.moveWorker(aWorkerBR, aWorkerBC, aWorkerBR+1, aWorkerBC, "A");
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR+1, aWorkerBC).getWorker().getPlayerName());

        // test moving workerB from tower to ground
        game.moveWorker(aWorkerBR+1, aWorkerBC, aWorkerBR+1, aWorkerBC+1, "A");
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerBR+1, aWorkerBC+1).getWorker().getPlayerName());
    }
//
    @Test
    public void testMoveWorkerException() throws Exception {
        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);

        try{
            game.moveWorker(2, 2, 3, 2, "A");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot move worker because no worker in specified location");
        }

        try{
            game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+2, aWorkerAC, "A");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "cannot move more than 1 cell");
        }

        try{
            game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "B");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "only worker owner can command the worker");
        }
//
        try{
            game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR-1, aWorkerAC, "A");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "make this move will take the worker out of the board");
        }
//
        try{
            game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC+1, "A");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "worker already exists in the about-to-move cell");
        }
//
        // hard code tower for testing purposes
        game.getBoard().getCell(aWorkerAR, aWorkerAC).setTower(new Tower(1));
        game.getBoard().getCell(aWorkerAR+1, aWorkerAC).setTower(new Tower(3));

        try{
            game.moveWorker(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot move more one level higher");
        }
//
        // hard code tower for testing purposes
        game.getBoard().getCell(aWorkerBR, aWorkerBC).setTower(new Tower(3));
        game.getBoard().getCell(aWorkerBR+1, aWorkerBC).setTower(new Tower(4));

        try{
            game.moveWorker(aWorkerBR, aWorkerBC, aWorkerBR+1, aWorkerBC, "A");
            fail("test moveWorker will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "cannot move to a tower with dome");
        }
//
        assertEquals(playerA.getName(), game.getBoard().getCell(aWorkerAR, aWorkerAC).getWorker().getPlayerName());
    }

    @Test
    public void testBuild() throws Exception {
        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);

        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", false);
        assertEquals(1, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());

        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", false);
        assertEquals(2, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());

        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", false);
        assertEquals(3, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());

        game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "A", true);
        assertEquals(4, game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTowerLevel());
        assertTrue(game.getBoard().getCell(aWorkerAR+1, aWorkerAC).getTower().hasDome());
    }

    @Test
    public void testBuildException() throws Exception {
        Worker aWorkerA = new Worker();
        int aWorkerAR = 0;
        int aWorkerAC = 0;

        Worker aWorkerB = new Worker();
        int aWorkerBR = 1;
        int aWorkerBC = 1;

        game.placeWorker(0, 0, aWorkerAR, aWorkerAC);
        game.placeWorker(0, 1, aWorkerBR, aWorkerBC);

        try{
            game.build(3, 3, 4, 3, "A", false);
            fail("test build will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot build because no worker in specified location");
        }

        try{
            game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC, "B", false);
            fail("test build will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot build because the worker does not belong to the player");
        }

        try{
            game.build(aWorkerAR, aWorkerAC, aWorkerAR-1, aWorkerAC, "A", false);
            fail("test build will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "can not build when location is out of the board");
        }

        try{
            game.build(aWorkerAR, aWorkerAC, aWorkerAR+2, aWorkerAC, "A", false);
            fail("test build will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "can only build in adjacent cell");
        }
//
        try{
            game.build(aWorkerAR, aWorkerAC, aWorkerAR+1, aWorkerAC+1, "A", false);
            fail("test build will have an error because exception should be triggered");
        } catch (Exception e){
            assertEquals(e.getMessage(), "Cannot build because the distinated location alreay has a worker");
        }
    }
}
