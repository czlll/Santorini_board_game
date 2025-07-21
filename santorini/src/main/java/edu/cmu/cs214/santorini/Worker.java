package edu.cmu.cs214.santorini;

/**
 * Represents a worker in the Santorini game.
 * Each player has two workers that can move and build.
 */
public class Worker {
  private Position position;
  private final String id;
  private final Player owner;

  /**
   * Creates a new worker with the given ID and owner.
   *
   * @param id the unique identifier for this worker
   * @param owner the player who owns this worker
   */
  public Worker(String id, Player owner) {
    this.id = id;
    this.owner = owner;
    this.position = null; // Position will be set during game initialization
  }

  /**
   * Gets the current position of this worker.
   *
   * @return the current position
   */
  public Position getPosition() {
    return position;
  }

  /**
   * Sets the position of this worker.
   *
   * @param position the new position
   */
  public void setPosition(Position position) {
    this.position = position;
  }

  /**
   * Gets the ID of this worker.
   *
   * @return the worker ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the player who owns this worker.
   *
   * @return the owner
   */
  public Player getOwner() {
    return owner;
  }

  /**
   * Moves this worker to the target position on the given board.
   * Updates both the worker's position and the board's occupation state.
   * This method checks God Card abilities for special movement rules.
   *
   * @param board the game board
   * @param target the target position
   * @return true if the move was successful, false otherwise
   */
  public boolean move(Board board, Position target) {
    if (position == null) {
      throw new IllegalStateException("Worker has not been placed on the board");
    }
    
    // Check if the move is allowed by the player's God Card
    boolean canMove = (owner.getGodCard() != null) 
        ? owner.getGodCard().canMove(board, this, target)
        : board.canMove(position, target);
    
    if (!canMove) {
      return false;
    }
    
    // Update the board's occupation state
    board.setOccupied(position, false);
    board.setOccupied(target, true);
    
    // Update the worker's position
    position = target;
    
    return true;
  }

  /**
   * Builds a block or dome at the target position on the given board.
   * This method checks God Card abilities for special building rules.
   *
   * @param board the game board
   * @param target the target position
   * @param isDome whether to build a dome (true) or a block (false)
   * @return true if the build was successful, false otherwise
   */
  public boolean build(Board board, Position target, boolean isDome) {
    if (position == null) {
      throw new IllegalStateException("Worker has not been placed on the board");
    }
    
    // Check if the build is allowed by the player's God Card
    boolean canBuild = (owner.getGodCard() != null) 
        ? owner.getGodCard().canBuild(board, this, target, isDome)
        : board.canBuild(position, target);
    
    if (!canBuild) {
      return false;
    }
    
    if (isDome) {
      return board.buildDome(target);
    } else {
      return board.buildBlock(target);
    }
  }

  @Override
  public String toString() {
    return "Worker " + id + " at " + position;
  }
}