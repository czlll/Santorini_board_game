package edu.cmu.cs214.santorini;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {
  private Board board;

  @Before
  public void setUp() {
    board = new Board();
  }

  @Test
  public void testInitialBoardState() {
    Position position = new Position(2, 3);
    assertEquals(0, board.getHeight(position));
    assertFalse(board.hasDome(position));
    assertFalse(board.isOccupied(position));
  }

  @Test
  public void testSetOccupied() {
    Position position = new Position(1, 1);
    assertFalse(board.isOccupied(position));
    
    board.setOccupied(position, true);
    assertTrue(board.isOccupied(position));
    
    board.setOccupied(position, false);
    assertFalse(board.isOccupied(position));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetOccupiedOutOfBounds() {
    board.setOccupied(new Position(-1, 0), true);
  }

  @Test
  public void testBuildBlock() {
    Position position = new Position(2, 2);
    
    // Build first level
    assertTrue(board.buildBlock(position));
    assertEquals(1, board.getHeight(position));
    
    // Build second level
    assertTrue(board.buildBlock(position));
    assertEquals(2, board.getHeight(position));
    
    // Build third level
    assertTrue(board.buildBlock(position));
    assertEquals(3, board.getHeight(position));
    
    // Cannot build beyond level 3
    assertFalse(board.buildBlock(position));
    assertEquals(3, board.getHeight(position));
  }

  @Test
  public void testBuildDome() {
    Position position = new Position(3, 3);
    
    // Build to level 3
    assertTrue(board.buildBlock(position));
    assertTrue(board.buildBlock(position));
    assertTrue(board.buildBlock(position));
    assertEquals(3, board.getHeight(position));
    
    // Build dome on level 3
    assertTrue(board.buildDome(position));
    assertTrue(board.hasDome(position));
    
    // Cannot build on a cell with a dome
    assertFalse(board.buildBlock(position));
    assertFalse(board.buildDome(position));
  }

  @Test
  public void testCannotBuildOnOccupiedCell() {
    Position position = new Position(1, 1);
    board.setOccupied(position, true);
    
    assertFalse(board.buildBlock(position));
    assertFalse(board.buildDome(position));
  }

  @Test
  public void testCanMove() {
    Position source = new Position(2, 2);
    Position target = new Position(3, 3);
    
    // Can move to adjacent cell at same level
    assertTrue(board.canMove(source, target));
    
    // Can move to adjacent cell one level higher
    board.buildBlock(target);
    assertTrue(board.canMove(source, target));
    
    // Cannot move to cell two levels higher
    board.buildBlock(target);
    assertFalse(board.canMove(source, target));
    
    // Cannot move to occupied cell
    board.setOccupied(target, true);
    assertFalse(board.canMove(source, target));
    
    // Cannot move to cell with dome
    Position domePosition = new Position(1, 1);
    board.buildBlock(domePosition);
    board.buildBlock(domePosition);
    board.buildBlock(domePosition);
    board.buildDome(domePosition);
    assertFalse(board.canMove(source, domePosition));
  }

  @Test
  public void testCanBuild() {
    Position workerPosition = new Position(2, 2);
    Position buildPosition = new Position(3, 3);
    
    // Can build on adjacent cell
    assertTrue(board.canBuild(workerPosition, buildPosition));
    
    // Cannot build on occupied cell
    board.setOccupied(buildPosition, true);
    assertFalse(board.canBuild(workerPosition, buildPosition));
    
    // Cannot build on cell with dome
    Position domePosition = new Position(1, 1);
    board.buildBlock(domePosition);
    board.buildBlock(domePosition);
    board.buildBlock(domePosition);
    board.buildDome(domePosition);
    assertFalse(board.canBuild(workerPosition, domePosition));
    
    // Cannot build on non-adjacent cell
    assertFalse(board.canBuild(workerPosition, new Position(4, 4)));
  }
}