package edu.cmu.cs214.hw3;

import edu.cmu.cs214.hw3.game.Cell;
import edu.cmu.cs214.hw3.game.Worker;
import org.junit.Test;
import static org.junit.Assert.*;

public class CellTest {

    @Test
    public void buildAndGetTowerLevelTest() throws Exception {
        Cell cell = new Cell(0, 0);
        cell.build(false);
        assertEquals(1, cell.getTowerLevel());
    }

    @Test
    public void hasWorkerTest(){
        Cell cell = new Cell(0, 0);
        cell.setWorker(new Worker());
        assertFalse(cell.hasWorker());
        cell.getWorker().setPlayerName("A");
        assertTrue(cell.hasWorker());
    }

}
