# Local E2E Testing Setup Guide

## Overview

This guide explains how to set up and run E2E tests locally for the Santorini Board Game.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (for downloading browser binaries)

## Setup Steps

### 1. Install Playwright Browsers

```bash
cd backend
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### 2. Test Framework Installation

Run the basic framework test to verify Playwright is working:

```bash
cd backend
mvn test -Dtest="PlaywrightFrameworkTest"
```

This test verifies:
- ✅ Browser launches correctly
- ✅ Page navigation works
- ✅ JavaScript execution works
- ✅ Screenshot capability works

## Running E2E Tests

### Framework Tests (No Server Required)

These tests verify the Playwright framework is working correctly:

```bash
# Run framework tests
mvn test -Dtest="PlaywrightFrameworkTest"

# Run with visible browser (for debugging)
mvn test -Dtest="PlaywrightFrameworkTest" -Dheadless=false
```

### Application E2E Tests (Server Required)

These tests require the Santorini application server to be running:

#### Step 1: Start the Application Server

```bash
# Terminal 1: Start the server
cd backend
mvn exec:java -Dexec.mainClass="edu.cmu.cs214.hw3.App"
```

Wait for the server to start (you should see server startup messages).

#### Step 2: Run E2E Tests

```bash
# Terminal 2: Run E2E tests
cd backend
mvn test -Dtest="Santorini*E2ETest"

# Run with visible browser (for debugging)
mvn test -Dtest="Santorini*E2ETest" -Dheadless=false -DslowMo=1000
```

## Test Categories

### 1. Framework Tests (`PlaywrightFrameworkTest`)
- **Purpose**: Verify Playwright installation and basic functionality
- **Requirements**: None (no server needed)
- **Run Command**: `mvn test -Dtest="PlaywrightFrameworkTest"`

### 2. Functional Tests (`SantoriniE2ETest`)
- **Purpose**: Test complete game workflows
- **Requirements**: Application server running on localhost:8080
- **Run Command**: `mvn test -Dtest="SantoriniE2ETest"`

### 3. Performance Tests (`SantoriniPerformanceE2ETest`)
- **Purpose**: Test performance and reliability
- **Requirements**: Application server running on localhost:8080
- **Run Command**: `mvn test -Dtest="SantoriniPerformanceE2ETest"`

## Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `headless` | `true` | Run browser in headless mode |
| `slowMo` | `0` | Slow motion delay (ms) for debugging |
| `browser` | `chromium` | Browser to use (chromium/firefox/webkit) |
| `baseUrl` | `http://localhost:8080` | Application base URL |

### Examples

```bash
# Debug mode with visible browser and slow motion
mvn test -Dtest="SantoriniE2ETest" -Dheadless=false -DslowMo=2000

# Use Firefox browser
mvn test -Dtest="SantoriniE2ETest" -Dbrowser=firefox

# Custom server URL
mvn test -Dtest="SantoriniE2ETest" -DbaseUrl=http://localhost:9090
```

## Troubleshooting

### Common Issues

#### 1. Browser Installation Failed
```bash
# Reinstall browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --force"
```

#### 2. Server Connection Failed
- Verify server is running: `curl http://localhost:8080`
- Check server logs for errors
- Ensure port 8080 is not blocked by firewall

#### 3. Test Timeouts
- Increase timeout in test configuration
- Check system resources (CPU, memory)
- Run with visible browser to see what's happening

#### 4. Permission Issues (Linux/Mac)
```bash
# Fix browser permissions
chmod +x ~/.cache/ms-playwright/*/chrome-linux/chrome
```

### Debug Mode

Run tests with visible browser and slow motion for debugging:

```bash
mvn test -Dtest="PlaywrightFrameworkTest" -Dheadless=false -DslowMo=1000
```

### Screenshots

Screenshots are automatically saved to `target/screenshots/` on test failures.

## CI/CD Integration

### GitHub Actions

The project includes automated E2E testing in GitHub Actions:

- **Framework Tests**: Run automatically on every push/PR
- **Application Tests**: Currently skipped in CI (require server setup)

### Local CI Simulation

To simulate CI environment locally:

```bash
# Run in headless mode like CI
mvn test -Dtest="PlaywrightFrameworkTest" -Dheadless=true

# Run all tests (requires server)
mvn clean compile test
```

## Development Workflow

### Adding New E2E Tests

1. **Create test method** in appropriate test class
2. **Use Page Object Model** for UI interactions
3. **Add proper assertions** and error handling
4. **Test locally** with visible browser first
5. **Verify in headless mode** before committing

### Best Practices

1. **Start with framework tests** to verify setup
2. **Use explicit waits** instead of sleep
3. **Take screenshots** on failures for debugging
4. **Keep tests independent** and isolated
5. **Use meaningful test names** and descriptions

## Support

For issues or questions:

1. Check this documentation
2. Review test logs and screenshots
3. Run framework tests to verify setup
4. Check GitHub Actions logs for CI issues

## Next Steps

Once the application server setup is automated, the full E2E test suite can be enabled in CI/CD for comprehensive testing on every commit.