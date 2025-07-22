import edu.cmu.cs214.hw3.GodCardTest.DemeterTest;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running DemeterTest...");
        
        DemeterTest test = new DemeterTest();
        
        try {
            // Run setup
            test.setGame();
            System.out.println("✓ Setup completed successfully");
            
            // Run demeterBuildTesting
            test.demeterBuildTesting();
            System.out.println("✓ demeterBuildTesting passed");
            
            // Run demeterBuildExceptionTesting
            test.setGame(); // Reset game state
            test.demeterBuildExceptionTesting();
            System.out.println("✓ demeterBuildExceptionTesting passed");
            
            // Run demeterSkipTesting
            test.setGame(); // Reset game state
            test.demeterSkipTesting();
            System.out.println("✓ demeterSkipTesting passed");
            
            System.out.println("\n🎉 All DemeterTest tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}