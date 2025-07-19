package edu.cmu.cs214.santorini;

import static org.junit.Assert.*;
import org.junit.Test;

public class PositionTest {

  @Test
  public void testPositionCreation() {
    Position position = new Position(2, 3);
    assertEquals(2, position.getX());
    assertEquals(3, position.getY());
  }

  @Test
  public void testIsAdjacentTo() {
    Position center = new Position(2, 2);
    
    // Test all adjacent positions
    assertTrue(center.isAdjacentTo(new Position(1, 1))); // Diagonal
    assertTrue(center.isAdjacentTo(new Position(1, 2))); // Left
    assertTrue(center.isAdjacentTo(new Position(1, 3))); // Diagonal
    assertTrue(center.isAdjacentTo(new Position(2, 1))); // Up
    assertTrue(center.isAdjacentTo(new Position(2, 3))); // Down
    assertTrue(center.isAdjacentTo(new Position(3, 1))); // Diagonal
    assertTrue(center.isAdjacentTo(new Position(3, 2))); // Right
    assertTrue(center.isAdjacentTo(new Position(3, 3))); // Diagonal
    
    // Test non-adjacent positions
    assertFalse(center.isAdjacentTo(new Position(0, 0)));
    assertFalse(center.isAdjacentTo(new Position(4, 4)));
    assertFalse(center.isAdjacentTo(new Position(2, 2))); // Same position
  }

  @Test
  public void testIsWithinBounds() {
    // Test positions within bounds
    assertTrue(new Position(0, 0).isWithinBounds());
    assertTrue(new Position(4, 4).isWithinBounds());
    assertTrue(new Position(2, 3).isWithinBounds());
    
    // Test positions outside bounds
    assertFalse(new Position(-1, 0).isWithinBounds());
    assertFalse(new Position(0, -1).isWithinBounds());
    assertFalse(new Position(5, 0).isWithinBounds());
    assertFalse(new Position(0, 5).isWithinBounds());
  }

  @Test
  public void testEquals() {
    Position pos1 = new Position(1, 2);
    Position pos2 = new Position(1, 2);
    Position pos3 = new Position(2, 1);
    
    assertEquals(pos1, pos2);
    assertNotEquals(pos1, pos3);
    assertNotEquals(pos1, null);
    assertNotEquals(pos1, "not a position");
  }

  @Test
  public void testHashCode() {
    Position pos1 = new Position(1, 2);
    Position pos2 = new Position(1, 2);
    
    assertEquals(pos1.hashCode(), pos2.hashCode());
  }

  @Test
  public void testToString() {
    Position position = new Position(2, 3);
    assertEquals("(2, 3)", position.toString());
  }
}