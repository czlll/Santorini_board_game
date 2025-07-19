package edu.cmu.cs214.santorini;

/**
 * Represents a position on the game board.
 */
public class Position {
  private final int x;
  private final int y;

  /**
   * Creates a new position with the given coordinates.
   *
   * @param x the x-coordinate (column)
   * @param y the y-coordinate (row)
   */
  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the x-coordinate (column) of this position.
   *
   * @return the x-coordinate
   */
  public int getX() {
    return x;
  }

  /**
   * Gets the y-coordinate (row) of this position.
   *
   * @return the y-coordinate
   */
  public int getY() {
    return y;
  }

  /**
   * Checks if this position is adjacent to the given position.
   * Two positions are adjacent if they are horizontally, vertically,
   * or diagonally next to each other.
   *
   * @param other the position to check adjacency with
   * @return true if the positions are adjacent, false otherwise
   */
  public boolean isAdjacentTo(Position other) {
    int dx = Math.abs(this.x - other.x);
    int dy = Math.abs(this.y - other.y);
    return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
  }

  /**
   * Checks if this position is within the bounds of the game board.
   *
   * @return true if the position is within bounds, false otherwise
   */
  public boolean isWithinBounds() {
    return x >= 0 && x < 5 && y >= 0 && y < 5;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Position position = (Position) obj;
    return x == position.x && y == position.y;
  }

  @Override
  public int hashCode() {
    return 31 * x + y;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}