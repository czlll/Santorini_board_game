# Santorini Game Test Suite

This document describes the comprehensive test suite for the Santorini board game, designed to validate all functional and non-functional requirements specified in the project requirements document.

## Test Structure

### Core Test Classes

#### 1. ComprehensiveGameTest.java
**Purpose**: Tests all core game functionality and functional requirements
**Coverage**:
- Worker placement phase (FR 3.1.3)
- Game turn mechanics (FR 3.1.4)
- Win conditions (FR 3.1.6)
- Building system
- Player turn management
- Board boundaries
- Worker ownership
- Game state consistency

#### 2. GameRulesTest.java
**Purpose**: Tests specific game rules and mechanics
**Coverage**:
- Game objectives (FR 3.1.1)
- Game components (FR 3.1.2)
- Movement restrictions
- Height restrictions
- Building restrictions
- Operation sequences (FR 3.1.5)
- Invalid operations (FR 3.1.7)
- Game end conditions (FR 3.1.8)

#### 3. UserInterfaceTest.java
**Purpose**: Tests user interface requirements and interactions
**Coverage**:
- Board display (FR 3.2)
- Worker position display
- Tower level display
- User interactions (FR 3.3)
- Game state management (FR 3.4)
- Error handling and feedback
- Game flow validation

#### 4. PerformanceTest.java
**Purpose**: Tests performance and non-functional requirements
**Coverage**:
- Performance requirements (NFR 4.1)
- Security requirements (NFR 4.2)
- Usability requirements (NFR 4.3)
- Compatibility requirements (NFR 4.4)
- Maintainability (NFR 4.5)
- Stress testing
- Error recovery

#### 5. EdgeCaseTest.java
**Purpose**: Tests boundary conditions and edge cases
**Coverage**:
- Board boundary conditions
- Tower height extremes
- Worker interaction edge cases
- Building pattern edge cases
- Game state edge cases
- Input validation
- Rapid operations

### Existing Test Classes

#### 6. GameTest.java
- Basic game functionality
- Worker placement and movement
- Building mechanics
- Exception handling

#### 7. IntegrationTest.java
- Complete game flow integration
- Multi-turn game scenarios
- Win condition validation

#### 8. UndoTest.java
- Undo functionality
- Game state restoration

#### 9. CellTest.java
- Individual cell functionality
- Tower mechanics

### God Card Test Classes

#### 10. DemeterTest.java
- Demeter god card functionality
- Double building mechanics

#### 11. MinotaurTest.java
- Minotaur god card functionality
- Worker pushing mechanics

#### 12. PanTest.java
- Pan god card functionality
- Alternative win conditions

#### 13. HermesTest.java
- Hermes god card functionality
- Multiple movement mechanics

#### 14. ApolloTest.java
- Apollo god card functionality
- Worker swapping mechanics

## Requirements Coverage

### Functional Requirements (Section 3)

| Requirement | Test Class | Test Methods |
|-------------|------------|--------------|
| 3.1.1 Game Objective | GameRulesTest | testWinConditionLevel3, testNoWinOnLevel2 |
| 3.1.2 Game Components | GameRulesTest | testBoardSize, testWorkerCount, testBuildingLevels |
| 3.1.3 Worker Placement | ComprehensiveGameTest | testWorkerPlacementPhase, testWorkerPlacementInvalidPositions |
| 3.1.4 Game Turn | ComprehensiveGameTest, GameRulesTest | testBasicGameTurn, testMandatoryMoveAndBuild |
| 3.1.5 Operation Sequence | GameRulesTest | testCorrectOperationSequence |
| 3.1.6 Win Conditions | ComprehensiveGameTest | testWinByReachingLevel3, testCannotMoveAfterGameEnds |
| 3.1.7 Invalid Operations | GameRulesTest | testInvalidOperations |
| 3.1.8 Game End | GameRulesTest | testGameEndOnWin |
| 3.2 User Interface | UserInterfaceTest | testBoardDisplay, testWorkerPositionDisplay |
| 3.3 User Interaction | UserInterfaceTest | testWorkerSelectionAndMovement, testBuildingSelection |
| 3.4 Game State Management | UserInterfaceTest | testTurnManagement, testGameStatusTracking |

### Non-Functional Requirements (Section 4)

| Requirement | Test Class | Test Methods |
|-------------|------------|--------------|
| 4.1 Performance | PerformanceTest | testGameInitializationTime, testGameOperationResponseTime |
| 4.2 Security | PerformanceTest | testGameStateIntegrity, testPlayerIsolation |
| 4.3 Usability | PerformanceTest | testClearErrorMessages, testGameStateVisibility |
| 4.4 Compatibility | PerformanceTest | testCrossPlatformCompatibility |
| 4.5 Maintainability | PerformanceTest | testCodeModularity, testErrorRecovery |

## Running the Tests

### Run All Tests
```bash
# Using Maven
mvn test

# Using specific test suite
mvn test -Dtest=AllTestsSuite
```

### Run Individual Test Classes
```bash
# Core functionality tests
mvn test -Dtest=ComprehensiveGameTest
mvn test -Dtest=GameRulesTest
mvn test -Dtest=UserInterfaceTest
mvn test -Dtest=PerformanceTest
mvn test -Dtest=EdgeCaseTest

# Existing tests
mvn test -Dtest=GameTest
mvn test -Dtest=IntegrationTest

# God card tests
mvn test -Dtest=DemeterTest
mvn test -Dtest=MinotaurTest
mvn test -Dtest=PanTest
```

### Run Specific Test Methods
```bash
# Test specific functionality
mvn test -Dtest=ComprehensiveGameTest#testWorkerPlacementPhase
mvn test -Dtest=GameRulesTest#testWinConditionLevel3
mvn test -Dtest=PerformanceTest#testGameInitializationTime
```

## Test Data and Setup

### Test Game Configurations
- **Basic Setup**: 2 players, 4 workers, empty 5x5 board
- **In-Progress Game**: Workers placed, ready for gameplay
- **Near-Win Scenarios**: Level 2 towers for testing win conditions
- **Complex Board States**: Various tower heights and worker positions

### Test Assertions
- **State Validation**: Game status, current player, current action
- **Board Validation**: Worker positions, tower levels, dome presence
- **Error Validation**: Exception messages, error conditions
- **Performance Validation**: Timing constraints, resource usage

## Expected Test Results

### Success Criteria
- All functional requirements validated
- All non-functional requirements met
- No critical bugs or edge case failures
- Performance benchmarks achieved
- Error handling comprehensive

### Performance Benchmarks
- Game initialization: < 5 seconds (as per requirements)
- Game operations: < 1 second response time (as per requirements)
- Memory usage: Reasonable for game complexity
- Error recovery: Graceful handling without corruption

## Test Maintenance

### Adding New Tests
1. Identify requirement or functionality to test
2. Choose appropriate test class or create new one
3. Follow existing naming conventions
4. Include comprehensive assertions
5. Update this documentation

### Test Best Practices
- Each test should be independent
- Use descriptive test method names
- Include both positive and negative test cases
- Test boundary conditions
- Validate both expected behavior and error conditions

## Troubleshooting

### Common Issues
- **Test Failures**: Check game state setup in @Before methods
- **Performance Issues**: Verify system resources and test data size
- **Compilation Errors**: Ensure all dependencies are available

### Debug Tips
- Use detailed assertion messages
- Log game state before and after operations
- Test individual components in isolation
- Verify test data setup is correct

## Coverage Report

To generate test coverage report:
```bash
mvn jacoco:report
```

The report will be available in `target/site/jacoco/index.html`

## Conclusion

This comprehensive test suite ensures that the Santorini game implementation meets all specified requirements and handles edge cases gracefully. The tests provide confidence in the game's reliability, performance, and user experience.