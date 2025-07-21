package edu.cmu.cs214.santorini;

import edu.cmu.cs214.santorini.godcards.GodCard;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Santorini game.
 * Each player has two workers that they control.
 */
public class Player {
  private final String name;
  private final List<Worker> workers;
  private GodCard godCard;

  /**
   * Creates a new player with the given name.
   *
   * @param name the player's name
   */
  public Player(String name) {
    this.name = name;
    this.workers = new ArrayList<>(2);
    
    // Create two workers for this player
    workers.add(new Worker(name + "-1", this));
    workers.add(new Worker(name + "-2", this));
  }

  /**
   * Gets the name of this player.
   *
   * @return the player's name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the list of workers owned by this player.
   *
   * @return the list of workers
   */
  public List<Worker> getWorkers() {
    return new ArrayList<>(workers);
  }

  /**
   * Gets a worker by its index (0 or 1).
   *
   * @param index the index of the worker
   * @return the worker at the given index
   * @throws IllegalArgumentException if the index is invalid
   */
  public Worker getWorker(int index) {
    if (index < 0 || index >= workers.size()) {
      throw new IllegalArgumentException("Invalid worker index: " + index);
    }
    return workers.get(index);
  }

  /**
   * Places a worker at the given position on the board during game setup.
   *
   * @param board the game board
   * @param workerIndex the index of the worker to place (0 or 1)
   * @param position the position to place the worker
   * @return true if the placement was successful, false otherwise
   */
  public boolean placeWorker(Board board, int workerIndex, Position position) {
    if (workerIndex < 0 || workerIndex >= workers.size()) {
      throw new IllegalArgumentException("Invalid worker index: " + workerIndex);
    }
    
    if (!position.isWithinBounds()) {
      return false;
    }
    
    if (board.isOccupied(position)) {
      return false;
    }
    
    Worker worker = workers.get(workerIndex);
    
    // If the worker is already placed, remove it from its current position
    if (worker.getPosition() != null) {
      board.setOccupied(worker.getPosition(), false);
    }
    
    // Place the worker at the new position
    worker.setPosition(position);
    board.setOccupied(position, true);
    
    return true;
  }

  /**
   * Sets the God Card for this player.
   *
   * @param godCard the God Card to assign to this player
   */
  public void setGodCard(GodCard godCard) {
    this.godCard = godCard;
  }

  /**
   * Gets the God Card assigned to this player.
   *
   * @return the God Card, or null if none is assigned
   */
  public GodCard getGodCard() {
    return godCard;
  }

  @Override
  public String toString() {
    return "Player " + name;
  }
}