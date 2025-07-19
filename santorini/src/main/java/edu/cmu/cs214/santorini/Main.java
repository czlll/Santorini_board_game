package edu.cmu.cs214.santorini;

/**
 * Main class to demonstrate the Santorini game.
 */
public class Main {
  /**
   * Main method to run a simple demonstration of the Santorini game.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    // Create a new game
    Game game = new Game();
    
    // Add two players
    Player playerA = new Player("A");
    Player playerB = new Player("B");
    game.addPlayer(playerA);
    game.addPlayer(playerB);
    
    System.out.println("Santorini Game Started");
    System.out.println("Current state: " + game.getState());
    
    // Place workers for player A
    game.placeWorker(0, new Position(0, 0));
    game.placeWorker(1, new Position(0, 1));
    
    // Place workers for player B
    game.placeWorker(0, new Position(4, 4));
    game.placeWorker(1, new Position(4, 3));
    
    System.out.println("All workers placed");
    System.out.println("Current state: " + game.getState());
    System.out.println("Current player: " + game.getCurrentPlayer().getName());
    
    // Player A's turn
    game.selectWorker(0);
    game.moveWorker(new Position(1, 0));
    game.build(new Position(0, 0), false);
    
    System.out.println("Player A moved and built");
    System.out.println("Current player: " + game.getCurrentPlayer().getName());
    
    // Player B's turn
    game.selectWorker(0);
    game.moveWorker(new Position(3, 4));
    game.build(new Position(4, 4), false);
    
    System.out.println("Player B moved and built");
    System.out.println("Current player: " + game.getCurrentPlayer().getName());
    
    // Continue the game with more moves...
    // This is just a simple demonstration
    
    System.out.println("Game demonstration completed");
  }
}