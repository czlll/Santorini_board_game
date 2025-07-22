package edu.cmu.cs214.hw3.e2e;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test to verify Playwright framework is working correctly
 * This test doesn't require the application server to be running
 */
public class PlaywrightFrameworkTest extends BaseE2ETest {
    
    @Test
    @DisplayName("Playwright Framework - Basic Browser Test")
    void testPlaywrightFrameworkWorking() {
        // Navigate to a simple page to test browser functionality
        page.navigate("data:text/html,<html><body><h1 id='test'>Playwright Test</h1></body></html>");
        
        // Verify we can interact with the page
        String title = page.locator("#test").textContent();
        assertEquals("Playwright Test", title);
        
        // Verify page object is working
        assertTrue(page.url().startsWith("data:text/html"));
    }
    
    @Test
    @DisplayName("Playwright Framework - JavaScript Execution Test")
    void testJavaScriptExecution() {
        // Create a simple HTML page with JavaScript
        String html = """
            <html>
            <body>
                <div id="result">Initial</div>
                <script>
                    setTimeout(() => {
                        document.getElementById('result').textContent = 'Updated';
                    }, 100);
                </script>
            </body>
            </html>
            """;
        
        page.navigate("data:text/html," + html);
        
        // Wait for JavaScript to execute
        page.waitForFunction("document.getElementById('result').textContent === 'Updated'");
        
        // Verify JavaScript execution worked
        String result = page.locator("#result").textContent();
        assertEquals("Updated", result);
    }
    
    @Test
    @DisplayName("Playwright Framework - Screenshot Capability Test")
    void testScreenshotCapability() {
        // Navigate to a simple page
        page.navigate("data:text/html,<html><body><h1>Screenshot Test</h1></body></html>");
        
        // Take a screenshot to verify the capability works
        try {
            takeScreenshot("framework-test");
            // If we get here, screenshot capability is working
            assertTrue(true, "Screenshot capability is working");
        } catch (Exception e) {
            fail("Screenshot capability failed: " + e.getMessage());
        }
    }
}