# Playwright E2E Testing Guide for Santorini Board Game

## Overview

This document provides comprehensive guidance for running and maintaining End-to-End (E2E) tests for the Santorini Board Game using Microsoft Playwright.

## What is E2E Testing?

End-to-End testing validates complete user workflows by automating browser interactions, ensuring the entire application stack works correctly from the user's perspective.

## Setup and Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (for downloading browser binaries)

### Installation Steps

1. **Install Playwright browsers** (first time only):
   ```bash
   cd backend
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
   ```

2. **Verify installation**:
   ```bash
   mvn test -Dtest="SantoriniE2ETest#testCompleteGameFlow"
   ```

## Test Structure

### Test Classes

- **`BaseE2ETest`**: Base class providing common setup and teardown
- **`SantoriniE2ETest`**: Main E2E test scenarios
- **`SantoriniPerformanceE2ETest`**: Performance and reliability tests
- **`SantoriniGamePage`**: Page Object Model for game interactions

### Test Categories

#### 1. Functional Tests
- Complete game flow (start to victory)
- God card special abilities
- Win condition validation
- Error handling

#### 2. Performance Tests
- Page load performance
- Operation responsiveness
- Memory leak detection
- Network latency handling

#### 3. Compatibility Tests
- Different screen sizes
- Browser compatibility
- JavaScript functionality

## Running Tests

### Basic Commands

```bash
# Run all E2E tests
mvn test -Dtest="*E2ETest"

# Run specific test class
mvn test -Dtest="SantoriniE2ETest"

# Run specific test method
mvn test -Dtest="SantoriniE2ETest#testCompleteGameFlow"
```

### Configuration Options

```bash
# Run with visible browser (for debugging)
mvn test -Dtest="*E2ETest" -Dheadless=false

# Run with slow motion (for debugging)
mvn test -Dtest="*E2ETest" -DslowMo=1000 -Dheadless=false

# Use different browser
mvn test -Dtest="*E2ETest" -Dbrowser=firefox

# Record videos
mvn test -Dtest="*E2ETest" -DrecordVideo=true

# Custom base URL
mvn test -Dtest="*E2ETest" -DbaseUrl=http://localhost:9090
```

## Test Scenarios

### 1. Complete Game Flow Test
**Purpose**: Validates entire game workflow from start to finish

**Steps**:
1. Navigate to game page
2. Start new game
3. Set player names
4. Select god cards
5. Place workers
6. Execute moves and builds
7. Verify game state

### 2. God Card Ability Tests
**Purpose**: Validates special abilities of each god card

**Covered God Cards**:
- **Apollo**: Worker position swapping
- **Demeter**: Double building capability
- **Pan**: Alternative win conditions
- **Minotaur**: Worker pushing mechanics
- **Hermes**: Enhanced movement

### 3. Win Condition Tests
**Purpose**: Validates all possible win scenarios

**Scenarios**:
- Reaching level 3 tower
- Pan's descent win condition
- Opponent unable to move

### 4. Error Handling Tests
**Purpose**: Validates proper error handling for invalid operations

**Scenarios**:
- Invalid moves
- Invalid builds
- Out-of-bounds operations
- Conflicting actions

## Page Object Model

The tests use the Page Object Model pattern for maintainable and reusable code:

```java
// Example usage
SantoriniGamePage gamePage = new SantoriniGamePage(page);
gamePage.navigateToGame();
gamePage.startNewGame();
gamePage.setPlayerNames("Alice", "Bob");
gamePage.selectGodCard("Apollo");
gamePage.placeWorker(0, 0);
gamePage.moveWorker(0, 0, 0, 1);
gamePage.buildTower(0, 2, false);
```

## Debugging and Troubleshooting

### Taking Screenshots
Tests automatically take screenshots on failures. Manual screenshots:
```java
gamePage.takeScreenshot("debug-state");
```

### Running in Debug Mode
```bash
# Visible browser with slow motion
mvn test -Dtest="SantoriniE2ETest" -Dheadless=false -DslowMo=2000
```

### Common Issues

1. **Test Timeouts**
   - Increase timeout values
   - Check if application server is running
   - Verify network connectivity

2. **Element Not Found**
   - Check HTML element selectors
   - Verify page load completion
   - Update locators if UI changed

3. **Browser Installation Issues**
   - Re-run browser installation
   - Check internet connectivity
   - Verify system permissions

## CI/CD Integration

### GitHub Actions Example
```yaml
name: E2E Tests
on: [push, pull_request]
jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
    - name: Install Playwright
      run: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
    - name: Run E2E tests
      run: mvn test -Dtest="*E2ETest"
    - name: Upload screenshots
      uses: actions/upload-artifact@v3
      if: failure()
      with:
        name: screenshots
        path: backend/target/screenshots/
```

## Best Practices

### 1. Test Independence
- Each test should be independent
- Use proper setup and teardown
- Avoid test dependencies

### 2. Reliable Selectors
- Use stable element selectors (IDs preferred)
- Avoid CSS selectors that may change
- Use data attributes for test elements

### 3. Explicit Waits
- Use explicit waits instead of sleep
- Wait for specific conditions
- Handle dynamic content properly

### 4. Test Data Management
- Use test-specific data
- Clean up after tests
- Avoid hardcoded values

### 5. Error Handling
- Implement proper error handling
- Take screenshots on failures
- Provide meaningful error messages

## Maintenance

### Regular Tasks
1. Update browser versions periodically
2. Review and update selectors when UI changes
3. Add tests for new features
4. Remove obsolete tests
5. Monitor test execution times

### Performance Monitoring
- Track test execution times
- Monitor resource usage
- Identify flaky tests
- Optimize slow tests

## Extending Tests

### Adding New Test Cases
1. Identify user workflow to test
2. Create test method in appropriate class
3. Use Page Object Model for interactions
4. Add proper assertions and error handling
5. Document the test purpose and steps

### Adding New Page Objects
1. Create new page class in `pages` package
2. Define element locators
3. Implement interaction methods
4. Add proper waiting mechanisms
5. Include error handling

## Support and Resources

- **Playwright Documentation**: https://playwright.dev/java/
- **Maven Surefire Plugin**: https://maven.apache.org/surefire/maven-surefire-plugin/
- **JUnit 5 Documentation**: https://junit.org/junit5/docs/current/user-guide/

## Conclusion

This E2E testing framework provides comprehensive coverage of the Santorini Board Game functionality, ensuring reliable user experiences and catching regressions early in the development cycle.

For questions or issues, please refer to the project documentation or create an issue in the repository.