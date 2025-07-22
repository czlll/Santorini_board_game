package edu.cmu.cs214.hw3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all comprehensive tests for the Santorini game
 * 
 * This suite includes:
 * - DocumentBasedTest: 基于测试文档的具体测试用例 (测试用例 1-18)
 * - WorkerMovementAndBuildingTest: 工人移动和建造功能专项测试
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
    // Document-based test classes (基于测试文档的测试用例)
    DocumentBasedTest.class,
    WorkerMovementAndBuildingTest.class,
    
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
    edu.cmu.cs214.hw3.GodCardTest.ApolloTest.class,
    edu.cmu.cs214.hw3.GodCardTest.HephaestusTest.class
})
public class AllTestsSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}