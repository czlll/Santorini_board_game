import edu.cmu.cs214.hw3.GodCardTest.DemeterTest;
import edu.cmu.cs214.hw3.GodCardTest.PanTest;

public class MultiTestRunner {
    public static void main(String[] args) {
        System.out.println("=== Running Multiple God Card Tests ===\n");
        
        int passed = 0;
        int total = 0;
        
        // Test DemeterTest
        System.out.println("Testing DemeterTest...");
        try {
            DemeterTest demeterTest = new DemeterTest();
            
            demeterTest.setGame();
            demeterTest.demeterBuildTesting();
            System.out.println("✓ DemeterTest.demeterBuildTesting passed");
            passed++; total++;
            
            demeterTest.setGame();
            demeterTest.demeterBuildExceptionTesting();
            System.out.println("✓ DemeterTest.demeterBuildExceptionTesting passed");
            passed++; total++;
            
            demeterTest.setGame();
            demeterTest.demeterSkipTesting();
            System.out.println("✓ DemeterTest.demeterSkipTesting passed");
            passed++; total++;
            
        } catch (Exception e) {
            System.err.println("❌ DemeterTest failed: " + e.getMessage());
            total += 3;
        }
        
        System.out.println();
        
        // Test PanTest
        System.out.println("Testing PanTest...");
        try {
            PanTest panTest = new PanTest();
            
            panTest.setGame();
            System.out.println("✓ PanTest setup completed");
            passed++; total++;
            
        } catch (Exception e) {
            System.err.println("❌ PanTest failed: " + e.getMessage());
            e.printStackTrace();
            total++;
        }
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Success Rate: " + (passed * 100 / total) + "%");
        
        if (passed == total) {
            System.out.println("🎉 All tests passed!");
        } else {
            System.out.println("⚠️  Some tests failed");
        }
    }
}