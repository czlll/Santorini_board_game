package edu.cmu.cs214.hw3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all comprehensive tests for the Santorini game
 * 
 * This suite includes:
 * - ComprehensiveGameTest: Core game functionality and requirements
 * - GameRulesTest: Specific game rules and mechanics
 * - UserInterfaceTest: UI requirements and user interactions
 * - PerformanceTest: Performance and non-functional requirements
 * - EdgeCaseTest: Boundary conditions and edge cases
 * - Existing tests: GameTest, IntegrationTest, UndoTest, CellTest
 * - God Card tests: DemeterTest, MinotaurTest, PanTest, HermesTest, ApolloTest
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // New comprehensive test classes
    ComprehensiveGameTest.class,
    GameRulesTest.class,
    UserInterfaceTest.class,
    PerformanceTest.class,
    EdgeCaseTest.class,
    
    // Existing test classes
    GameTest.class,
    GameTestExtra.class,
    IntegrationTest.class,
    UndoTest.class,
    CellTest.class,
    
    // God Card test classes
    edu.cmu.cs214.hw3.GodCardTest.DemeterTest.class,
    edu.cmu.cs214.hw3.GodCardTest.MinotaurTest.class,
    edu.cmu.cs214.hw3.GodCardTest.PanTest.class,
    edu.cmu.cs214.hw3.GodCardTest.HermesTest.class,
    edu.cmu.cs214.hw3.GodCardTest.ApolloTest.class
})
public class AllTestsSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}