package edu.cmu.cs214.santorini;

import edu.cmu.cs214.santorini.godcards.GodCard;
import edu.cmu.cs214.santorini.godcards.GodCardRegistry;
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
  private Position lastBuildPosition; // For God Card abilities
  private GodCard.BuildResult pendingBuildResult; // For additional builds

  /**
   * Enum representing the possible states of the game.
   */
  public enum GameState {
    CARD_SELECTION, // Players are selecting their God Cards
    SETUP, // Players are placing their workers
    MOVE, // Current player needs to move a worker
    BUILD, // Current player needs to build after moving
    ADDITIONAL_BUILD, // Current player can build again (God Card ability)
    GAME_OVER // Game is over, there is a winner
  }

  /**
   * Creates a new Santorini game.
   */
  public Game() {
    this.board = new Board();
    this.players = new ArrayList<>(2);
    this.state = GameState.CARD_SELECTION;
    this.winner = null;
    this.selectedWorker = null;
    this.lastBuildPosition = null;
    this.pendingBuildResult = GodCard.BuildResult.NONE;
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
   * Gets the last build position (used for God Card abilities).
   *
   * @return the last build position, or null if none
   */
  public Position getLastBuildPosition() {
    return lastBuildPosition;
  }

  /**
   * Gets the pending build result (used for God Card abilities).
   *
   * @return the pending build result
   */
  public GodCard.BuildResult getPendingBuildResult() {
    return pendingBuildResult;
  }

  /**
   * Assigns a God Card to a player.
   *
   * @param playerIndex the index of the player (0 or 1)
   * @param cardName the name of the God Card
   * @return true if the assignment was successful, false otherwise
   */
  public boolean assignGodCard(int playerIndex, String cardName) {
    if (state != GameState.CARD_SELECTION) {
      return false;
    }
    
    if (playerIndex < 0 || playerIndex >= players.size()) {
      return false;
    }
    
    GodCard card = GodCardRegistry.createCard(cardName);
    if (card == null) {
      return false;
    }
    
    players.get(playerIndex).setGodCard(card);
    
    // Check if both players have selected cards
    boolean allCardsSelected = true;
    for (Player player : players) {
      if (player.getGodCard() == null) {
        allCardsSelected = false;
        break;
      }
    }
    
    if (allCardsSelected) {
      state = GameState.SETUP;
      currentPlayer = players.get(0); // Player A starts
    }
    
    return true;
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
    
    Position originalPosition = selectedWorker.getPosition();
    
    // Handle Minotaur's push ability
    Worker pushedWorker = null;
    Position pushPosition = null;
    if (board.isOccupied(target) && currentPlayer.getGodCard() != null 
        && "Minotaur".equals(currentPlayer.getGodCard().getName())) {
      // Find the worker at the target position
      pushedWorker = findWorkerAtPosition(target);
      if (pushedWorker != null && pushedWorker.getOwner() != currentPlayer) {
        // Calculate push position
        int deltaX = target.getX() - originalPosition.getX();
        int deltaY = target.getY() - originalPosition.getY();
        pushPosition = new Position(target.getX() + deltaX, target.getY() + deltaY);
      }
    }
    
    boolean success = selectedWorker.move(board, target);
    
    if (success) {
      // Handle Minotaur push
      if (pushedWorker != null && pushPosition != null) {
        pushedWorker.setPosition(pushPosition);
        board.setOccupied(pushPosition, true);
      }
      
      // Check for God Card win conditions
      boolean godCardWin = false;
      if (currentPlayer.getGodCard() != null) {
        godCardWin = currentPlayer.getGodCard().onAfterMove(board, selectedWorker, originalPosition, target);
      }
      
      // Check if the worker has reached a level 3 tower (standard win condition)
      if (board.getHeight(target) == 3 || godCardWin) {
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
   * Finds the worker at the specified position.
   *
   * @param position the position to search
   * @return the worker at the position, or null if none found
   */
  private Worker findWorkerAtPosition(Position position) {
    for (Player player : players) {
      for (Worker worker : player.getWorkers()) {
        if (position.equals(worker.getPosition())) {
          return worker;
        }
      }
    }
    return null;
  }

  /**
   * Builds a block or dome at the target position.
   *
   * @param target the target position
   * @param isDome whether to build a dome (true) or a block (false)
   * @return true if the build was successful, false otherwise
   */
  public boolean build(Position target, boolean isDome) {
    if ((state != GameState.BUILD && state != GameState.ADDITIONAL_BUILD) || selectedWorker == null) {
      return false;
    }
    
    // For additional builds, check restrictions
    if (state == GameState.ADDITIONAL_BUILD) {
      if (pendingBuildResult == GodCard.BuildResult.ADDITIONAL_BUILD_SAME) {
        // Hephaestus: must build on same position, cannot build dome
        if (!target.equals(lastBuildPosition) || isDome) {
          return false;
        }
      } else if (pendingBuildResult == GodCard.BuildResult.ADDITIONAL_BUILD_DIFF) {
        // Demeter: must build on different position, cannot build dome
        if (target.equals(lastBuildPosition) || isDome) {
          return false;
        }
      }
    }
    
    boolean success = selectedWorker.build(board, target, isDome);
    
    if (success) {
      lastBuildPosition = target;
      
      // Check for God Card additional build abilities
      if (state == GameState.BUILD && currentPlayer.getGodCard() != null) {
        pendingBuildResult = currentPlayer.getGodCard().onAfterBuild(board, selectedWorker, target, isDome);
        
        if (pendingBuildResult != GodCard.BuildResult.NONE) {
          state = GameState.ADDITIONAL_BUILD;
          return true; // Don't end turn yet
        }
      }
      
      // Reset state and end turn
      selectedWorker = null;
      lastBuildPosition = null;
      pendingBuildResult = GodCard.BuildResult.NONE;
      
      // Switch to the next player and transition back to the move phase
      switchPlayer();
      state = GameState.MOVE;
    }
    
    return success;
  }
  
  /**
   * Skips the additional build phase (for God Card abilities).
   *
   * @return true if the skip was successful, false otherwise
   */
  public boolean skipAdditionalBuild() {
    if (state != GameState.ADDITIONAL_BUILD) {
      return false;
    }
    
    // Reset state and end turn
    selectedWorker = null;
    lastBuildPosition = null;
    pendingBuildResult = GodCard.BuildResult.NONE;
    
    // Switch to the next player and transition back to the move phase
    switchPlayer();
    state = GameState.MOVE;
    
    return true;
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