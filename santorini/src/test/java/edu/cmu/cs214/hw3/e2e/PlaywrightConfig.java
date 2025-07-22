package edu.cmu.cs214.hw3.e2e;

/**
 * Configuration class for Playwright E2E tests
 * Provides centralized configuration management
 */
public class PlaywrightConfig {
    
    /**
     * Whether to run tests in headless mode
     */
    public static final boolean HEADLESS = Boolean.parseBoolean(
        System.getProperty("headless", "true"));
    
    /**
     * Slow motion delay in milliseconds for debugging
     */
    public static final int SLOW_MO = Integer.parseInt(
        System.getProperty("slowMo", "0"));
    
    /**
     * Browser to use for testing (chromium, firefox, webkit)
     */
    public static final String BROWSER = System.getProperty("browser", "chromium");
    
    /**
     * Whether to record videos of test execution
     */
    public static final boolean RECORD_VIDEO = Boolean.parseBoolean(
        System.getProperty("recordVideo", "false"));
    
    /**
     * Directory for storing screenshots
     */
    public static final String SCREENSHOTS_DIR = "target/screenshots";
    
    /**
     * Directory for storing videos
     */
    public static final String VIDEOS_DIR = "target/videos";
    
    /**
     * Default timeout for page operations in milliseconds
     */
    public static final int DEFAULT_TIMEOUT = 30000;
    
    /**
     * Base URL for the application
     */
    public static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:8080");
}