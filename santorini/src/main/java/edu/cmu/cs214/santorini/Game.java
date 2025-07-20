package edu.cmu.cs214.santorini;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Santorini game.
 * Manages the game state, players, and game flow.
 */
public class Game {
  private final Board board;
  private final List<Player> players;
  private Player currentPlayer;
  private Player winner;
  private Worker selectedWorker;
  private GameState state;

  /**
   * Enum representing the possible states of the game.
   */
  public enum GameState {
    SETUP, // Players are placing their workers
    MOVE, // Current player needs to move a worker
    BUILD, // Current player needs to build after moving
    GAME_OVER // Game is over, there is a winner
  }

  /**
   * Creates a new Santorini game.
   */
  public Game() {
    this.board = new Board();
    this.players = new ArrayList<>(2);
    this.state = GameState.SETUP;
    this.winner = null;
    this.selectedWorker = null;
  }

  /**
   * Adds a player to the game.
   *
   * @param player the player to add
   * @return true if the player was added, false if the game already has 2 players
   */
  public boolean addPlayer(Player player) {
    if (players.size() >= 2) {
      return false;
    }
    
    players.add(player);
    
    // If this is the first player, set them as the current player
    if (players.size() == 1) {
      currentPlayer = player;
    }
    
    return true;
  }

  /**
   * Gets the current state of the game.
   *
   * @return the game state
   */
  public GameState getState() {
    return state;
  }

  /**
   * Gets the currently selected worker.
   *
   * @return the selected worker, or null if none is selected
   */
  public Worker getSelectedWorker() {
    return selectedWorker;
  }

  /**
   * Gets the current player.
   *
   * @return the current player
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Gets the winner of the game, or null if there is no winner yet.
   *
   * @return the winner, or null if there is no winner
   */
  public Player getWinner() {
    return winner;
  }

  /**
   * Gets the game board.
   *
   * @return the game board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Gets the list of players.
   *
   * @return the list of players
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }



  /**
   * Places a worker for the current player during the setup phase.
   *
   * @param workerIndex the index of the worker to place (0 or 1)
   * @param position the position to place the worker
   * @return true if the placement was successful, false otherwise
   */
  public boolean placeWorker(int workerIndex, Position position) {
    if (state != GameState.SETUP) {
      return false;
    }
    
    boolean success = currentPlayer.placeWorker(board, workerIndex, position);
    
    if (success) {
      // Check if all workers have been placed
      boolean allWorkersPlaced = true;
      for (Player player : players) {
        for (Worker worker : player.getWorkers()) {
          if (worker.getPosition() == null) {
            allWorkersPlaced = false;
            break;
          }
        }
      }
      
      // If all workers are placed, transition to the move phase
      if (allWorkersPlaced) {
        state = GameState.MOVE;
        // Make sure player A starts the game as per the requirements
        currentPlayer = players.get(0);
      } else {
        // Switch to the next player
        switchPlayer();
      }
    }
    
    return success;
  }

  /**
   * Selects a worker for the current player during the move phase.
   *
   * @param workerIndex the index of the worker to select (0 or 1)
   * @return true if the worker was selected, false otherwise
   */
  public boolean selectWorker(int workerIndex) {
    if (state != GameState.MOVE) {
      return false;
    }
    
    try {
      selectedWorker = currentPlayer.getWorker(workerIndex);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Moves the selected worker to the target position.
   *
   * @param target the target position
   * @return true if the move was successful, false otherwise
   */
  public boolean moveWorker(Position target) {
    if (state != GameState.MOVE || selectedWorker == null) {
      return false;
    }
    
    boolean success = selectedWorker.move(board, target);
    
    if (success) {
      // Check if the worker has reached a level 3 tower (win condition)
      if (board.getHeight(target) == 3) {
        winner = currentPlayer;
        state = GameState.GAME_OVER;
      } else {
        // Transition to the build phase
        state = GameState.BUILD;
      }
    }
    
    return success;
  }

  /**
   * Builds a block or dome at the target position.
   *
   * @param target the target position
   * @param isDome whether to build a dome (true) or a block (false)
   * @return true if the build was successful, false otherwise
   */
  public boolean build(Position target, boolean isDome) {
    if (state != GameState.BUILD || selectedWorker == null) {
      return false;
    }
    
    boolean success = selectedWorker.build(board, target, isDome);
    
    if (success) {
      // Reset the selected worker
      selectedWorker = null;
      
      // Switch to the next player and transition back to the move phase
      switchPlayer();
      state = GameState.MOVE;
    }
    
    return success;
  }

  /**
   * Switches to the next player.
   */
  private void switchPlayer() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % players.size();
    currentPlayer = players.get(nextIndex);
  }

  /**
   * Checks if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  public boolean isGameOver() {
    return state == GameState.GAME_OVER;
  }
}