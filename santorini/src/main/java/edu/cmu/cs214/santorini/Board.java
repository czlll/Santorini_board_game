package edu.cmu.cs214.santorini;

/**
 * Represents the game board in Santorini.
 * The board is a 5x5 grid where each cell can have a tower of varying height.
 */
public class Board {
  private static final int BOARD_SIZE = 5;
  private static final int MAX_HEIGHT = 4; // Level 3 + dome
  
  // Heights of towers on the board (0 = ground, 1-3 = levels, 4 = dome)
  private final int[][] heights;
  
  // Whether a cell has a dome (level 4)
  private final boolean[][] hasDome;
  
  // Whether a cell is occupied by a worker
  private final boolean[][] isOccupied;

  /**
   * Creates a new empty board.
   */
  public Board() {
    heights = new int[BOARD_SIZE][BOARD_SIZE];
    hasDome = new boolean[BOARD_SIZE][BOARD_SIZE];
    isOccupied = new boolean[BOARD_SIZE][BOARD_SIZE];
  }

  /**
   * Gets the height of the tower at the given position.
   *
   * @param position the position to check
   * @return the height of the tower (0-3, ground level is 0)
   */
  public int getHeight(Position position) {
    if (!position.isWithinBounds()) {
      throw new IllegalArgumentException("Position is out of bounds: " + position);
    }
    return heights[position.getX()][position.getY()];
  }

  /**
   * Checks if the cell at the given position has a dome.
   *
   * @param position the position to check
   * @return true if the cell has a dome, false otherwise
   */
  public boolean hasDome(Position position) {
    if (!position.isWithinBounds()) {
      throw new IllegalArgumentException("Position is out of bounds: " + position);
    }
    return hasDome[position.getX()][position.getY()];
  }

  /**
   * Checks if the cell at the given position is occupied by a worker.
   *
   * @param position the position to check
   * @return true if the cell is occupied, false otherwise
   */
  public boolean isOccupied(Position position) {
    if (!position.isWithinBounds()) {
      throw new IllegalArgumentException("Position is out of bounds: " + position);
    }
    return isOccupied[position.getX()][position.getY()];
  }

  /**
   * Sets whether the cell at the given position is occupied by a worker.
   *
   * @param position the position to update
   * @param occupied whether the cell is occupied
   */
  public void setOccupied(Position position, boolean occupied) {
    if (!position.isWithinBounds()) {
      throw new IllegalArgumentException("Position is out of bounds: " + position);
    }
    isOccupied[position.getX()][position.getY()] = occupied;
  }

  /**
   * Builds a block on the tower at the given position.
   * This increases the height by 1.
   *
   * @param position the position to build on
   * @return true if the build was successful, false otherwise
   */
  public boolean buildBlock(Position position) {
    if (!position.isWithinBounds()) {
      throw new IllegalArgumentException("Position is out of bounds: " + position);
    }
    
    int x = position.getX();
    int y = position.getY();
    
    // Cannot build if the cell is occupied or has a dome
    if (isOccupied[x][y] || hasDome[x][y]) {
      return false;
    }
    
    // Cannot build beyond level 3
    if (heights[x][y] >= 3) {
      return false;
    }
    
    heights[x][y]++;
    return true;
  }

  /**
   * Builds a dome on the tower at the given position.
   * A dome can only be built on a level 3 tower.
   *
   * @param position the position to build on
   * @return true if the build was successful, false otherwise
   */
  public boolean buildDome(Position position) {
    if (!position.isWithinBounds()) {
      throw new IllegalArgumentException("Position is out of bounds: " + position);
    }
    
    int x = position.getX();
    int y = position.getY();
    
    // Cannot build if the cell is occupied or already has a dome
    if (isOccupied[x][y] || hasDome[x][y]) {
      return false;
    }
    
    // Can only build a dome on level 3
    if (heights[x][y] != 3) {
      return false;
    }
    
    hasDome[x][y] = true;
    return true;
  }

  /**
   * Checks if a worker can move from the source position to the target position.
   * A worker can move to an adjacent unoccupied cell without a dome,
   * and can only climb up one level.
   *
   * @param source the current position of the worker
   * @param target the target position to move to
   * @return true if the move is valid, false otherwise
   */
  public boolean canMove(Position source, Position target) {
    if (!source.isWithinBounds() || !target.isWithinBounds()) {
      return false;
    }
    
    // Must move to an adjacent cell
    if (!source.isAdjacentTo(target)) {
      return false;
    }
    
    int targetX = target.getX();
    int targetY = target.getY();
    
    // Cannot move to an occupied cell or a cell with a dome
    if (isOccupied[targetX][targetY] || hasDome[targetX][targetY]) {
      return false;
    }
    
    // Cannot climb more than one level
    int heightDifference = heights[targetX][targetY] - heights[source.getX()][source.getY()];
    return heightDifference <= 1;
  }

  /**
   * Checks if a worker can build at the target position.
   * A worker can build on an adjacent unoccupied cell without a dome.
   *
   * @param workerPosition the position of the worker
   * @param buildPosition the position to build on
   * @return true if the build is valid, false otherwise
   */
  public boolean canBuild(Position workerPosition, Position buildPosition) {
    if (!workerPosition.isWithinBounds() || !buildPosition.isWithinBounds()) {
      return false;
    }
    
    // Must build on an adjacent cell
    if (!workerPosition.isAdjacentTo(buildPosition)) {
      return false;
    }
    
    int buildX = buildPosition.getX();
    int buildY = buildPosition.getY();
    
    // Cannot build on an occupied cell or a cell with a dome
    return !isOccupied[buildX][buildY] && !hasDome[buildX][buildY];
  }

  /**
   * Moves a worker from one position to another, updating the occupation state.
   * This is used for special abilities like Minotaur's push.
   *
   * @param from the current position
   * @param to the target position
   */
  public void moveWorkerPosition(Position from, Position to) {
    if (!from.isWithinBounds() || !to.isWithinBounds()) {
      throw new IllegalArgumentException("Positions must be within bounds");
    }
    
    setOccupied(from, false);
    setOccupied(to, true);
  }
}