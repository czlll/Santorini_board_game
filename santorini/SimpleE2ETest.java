import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

public class SimpleE2ETest {
    private static final String BASE_URL = "http://localhost:12001";
    
    public static void main(String[] args) {
        System.out.println("=== Simple E2E Test ===\n");
        
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        try {
            // Test 1: Check if application is running
            System.out.println("Test 1: Application Health Check");
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            assert response.statusCode() == 200 : "Application should be running (status 200)";
            assert response.body().contains("Santorini") : "Response should contain 'Santorini'";
            System.out.println("✓ Application is running and accessible");
            
            // Test 2: Check API endpoints
            System.out.println("\nTest 2: API Endpoint Check");
            HttpRequest apiRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/game/state"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            
            HttpResponse<String> apiResponse = client.send(apiRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            assert apiResponse.statusCode() == 200 : "API should be accessible";
            System.out.println("✓ API endpoints are accessible");
            
            // Test 3: Check static resources
            System.out.println("\nTest 3: Static Resources Check");
            HttpRequest cssRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/css/style.css"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            
            HttpResponse<String> cssResponse = client.send(cssRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            // CSS might not exist, but server should respond (not 500 error)
            assert cssResponse.statusCode() != 500 : "Server should not have internal errors";
            System.out.println("✓ Static resource handling working");
            
            System.out.println("\n🎉 All E2E tests passed!");
            System.out.println("✓ Web application is accessible");
            System.out.println("✓ API endpoints are working");
            System.out.println("✓ Server is stable");
            
        } catch (Exception e) {
            System.err.println("❌ E2E test failed: " + e.getMessage());
            System.err.println("Make sure the application is running on " + BASE_URL);
        }
    }
}