# E2E Testing with Playwright

## Quick Start

### 1. Install Playwright Browsers
```bash
cd backend
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### 2. Run E2E Tests
```bash
# Run all E2E tests
mvn test -Dtest="*E2ETest"

# Run with visible browser (for debugging)
mvn test -Dtest="*E2ETest" -Dheadless=false

# Run specific test
mvn test -Dtest="SantoriniE2ETest#testCompleteGameFlow"
```

## Test Coverage

### ✅ Functional Tests
- **Complete Game Flow**: Start → God Card Selection → Worker Placement → Game Play → Victory
- **Apollo God Card**: Worker position swapping ability
- **Demeter God Card**: Double building capability  
- **Win Conditions**: Level 3 tower victory, Pan descent victory
- **Error Handling**: Invalid moves, boundary conditions

### ✅ Performance Tests
- **Page Load Performance**: < 3 seconds load time
- **Operation Responsiveness**: Rapid user interactions
- **Memory Leak Detection**: Long-duration game sessions
- **Network Latency**: Slow connection simulation

### ✅ Compatibility Tests
- **Responsive Design**: Desktop, tablet, mobile viewports
- **Browser Compatibility**: Chrome, Firefox, Safari, Edge
- **JavaScript Functionality**: Error detection and handling

## Test Architecture

```
backend/src/test/java/edu/cmu/cs214/hw3/e2e/
├── BaseE2ETest.java              # Base test setup/teardown
├── SantoriniE2ETest.java         # Main functional tests
├── SantoriniPerformanceE2ETest.java  # Performance tests
├── PlaywrightConfig.java        # Configuration management
└── pages/
    └── SantoriniGamePage.java    # Page Object Model
```

## Key Features

- **Page Object Model**: Maintainable and reusable test code
- **Automatic Screenshots**: Captured on test failures
- **Cross-Browser Testing**: Chrome, Firefox, Safari, Edge support
- **CI/CD Integration**: GitHub Actions workflow included
- **Performance Monitoring**: Load time and responsiveness validation
- **Error Detection**: JavaScript error monitoring

## Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `headless` | `true` | Run browser in headless mode |
| `slowMo` | `0` | Slow motion delay (ms) for debugging |
| `browser` | `chromium` | Browser to use (chromium/firefox/webkit) |
| `recordVideo` | `false` | Record test execution videos |
| `baseUrl` | `http://localhost:8080` | Application base URL |

## Example Usage

```java
@Test
void testGameFlow() {
    SantoriniGamePage gamePage = new SantoriniGamePage(page);
    
    // Navigate and start game
    gamePage.navigateToGame();
    gamePage.startNewGame();
    
    // Setup players and god cards
    gamePage.setPlayerNames("Alice", "Bob");
    gamePage.selectGodCard("Apollo");
    gamePage.selectGodCard("Demeter");
    
    // Place workers
    gamePage.placeWorker(0, 0);
    gamePage.placeWorker(1, 1);
    
    // Execute game moves
    gamePage.moveWorker(0, 0, 0, 1);
    gamePage.buildTower(0, 2, false);
    
    // Verify game state
    assertTrue(gamePage.hasWorkerAt(0, 1, "Alice"));
    assertEquals(1, gamePage.getTowerLevel(0, 2));
}
```

## Debugging Tips

1. **Visual Debugging**: Use `-Dheadless=false -DslowMo=1000`
2. **Screenshots**: Automatically saved to `target/screenshots/`
3. **Console Logs**: Monitor browser console for JavaScript errors
4. **Network Inspection**: Use Playwright's network interception
5. **Element Inspection**: Use browser dev tools with visible mode

## CI/CD Integration

The project includes a GitHub Actions workflow (`.github/workflows/e2e-tests.yml`) that:
- Runs on push/PR to main branch
- Installs Playwright browsers
- Executes all E2E tests
- Uploads screenshots on failure
- Generates test reports

## Benefits Over Unit Tests

| Aspect | Unit Tests | E2E Tests |
|--------|------------|-----------|
| **Scope** | Individual functions | Complete user workflows |
| **Perspective** | Developer view | User view |
| **Confidence** | Code correctness | User experience |
| **Integration** | Isolated components | Full system integration |
| **UI Coverage** | None | Complete UI validation |

## Maintenance

- **Regular Updates**: Keep Playwright version current
- **Selector Maintenance**: Update selectors when UI changes  
- **Performance Monitoring**: Track test execution times
- **Flaky Test Detection**: Identify and fix unstable tests
- **Coverage Analysis**: Ensure critical user paths are tested

For detailed documentation, see [E2E_TESTING_GUIDE.md](E2E_TESTING_GUIDE.md).