import edu.cmu.cs214.santorini.*;
import edu.cmu.cs214.santorini.godcards.GodCardRegistry;

public class SimpleIntegrationTest {
    public static void main(String[] args) {
        System.out.println("=== Simple Integration Test ===\n");
        
        try {
            // Test 1: Game Creation and Player Addition
            System.out.println("Test 1: Game Creation and Player Addition");
            Game game = new Game();
            Player playerA = new Player("Alice");
            Player playerB = new Player("Bob");
            
            game.addPlayer(playerA);
            game.addPlayer(playerB);
            
            assert game.getPlayers().size() == 2 : "Should have 2 players";
            assert game.getState() == Game.GameState.CARD_SELECTION : "Should be in CARD_SELECTION state";
            System.out.println("✓ Game creation and player addition successful");
            
            // Test 2: God Card Assignment
            System.out.println("\nTest 2: God Card Assignment");
            boolean result1 = game.assignGodCard(0, "Demeter");
            boolean result2 = game.assignGodCard(1, "Pan");
            
            assert result1 : "Should successfully assign Demeter to player 0";
            assert result2 : "Should successfully assign Pan to player 1";
            assert game.getState() == Game.GameState.SETUP : "Should transition to SETUP state";
            System.out.println("✓ God Card assignment successful");
            
            // Test 3: Worker Placement
            System.out.println("\nTest 3: Worker Placement");
            game.placeWorker(0, new Position(0, 0)); // Player A worker 0
            game.placeWorker(0, new Position(2, 2)); // Player B worker 0
            game.placeWorker(1, new Position(1, 1)); // Player A worker 1
            game.placeWorker(1, new Position(3, 3)); // Player B worker 1
            
            assert game.getState() == Game.GameState.MOVE : "Should transition to MOVE state";
            assert game.getCurrentPlayer() == playerA : "Player A should start";
            System.out.println("✓ Worker placement successful");
            
            // Test 4: Worker Movement
            System.out.println("\nTest 4: Worker Movement");
            game.selectWorker(0); // Select Player A's first worker
            boolean moveResult = game.moveWorker(new Position(0, 1)); // Move to adjacent position
            
            assert moveResult : "Move should be successful";
            assert game.getState() == Game.GameState.BUILD : "Should transition to BUILD state";
            System.out.println("✓ Worker movement successful");
            
            // Test 5: Building
            System.out.println("\nTest 5: Building");
            boolean buildResult = game.build(new Position(0, 0), false); // Build at original position
            
            assert buildResult : "Build should be successful";
            assert game.getBoard().getHeight(new Position(0, 0)) == 1 : "Height should be 1";
            System.out.println("✓ Building successful");
            
            // Test 6: God Card Registry
            System.out.println("\nTest 6: God Card Registry");
            var demeterCard = GodCardRegistry.createCard("Demeter");
            var panCard = GodCardRegistry.createCard("Pan");
            var minotaurCard = GodCardRegistry.createCard("Minotaur");
            
            assert demeterCard != null : "Should create Demeter card";
            assert panCard != null : "Should create Pan card";
            assert minotaurCard != null : "Should create Minotaur card";
            assert "Demeter".equals(demeterCard.getName()) : "Demeter card should have correct name";
            System.out.println("✓ God Card Registry working");
            
            System.out.println("\n🎉 All integration tests passed!");
            System.out.println("✓ Game state management working");
            System.out.println("✓ Player management working");
            System.out.println("✓ God Card system working");
            System.out.println("✓ Worker placement working");
            System.out.println("✓ Movement and building working");
            System.out.println("✓ Board state tracking working");
            
        } catch (Exception e) {
            System.err.println("❌ Integration test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}