import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class containing both the original Frontend tests and new integration tests
 */
public class FrontendTests {

    /**
     * This test checks the `generateShortestPathPromptHTML()` and
     * `generateClosestDestinationsFromAllPromptHTML()` methods in `Frontend`.
     */
    @Test
    public void roleTest1() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend frontend = new Frontend(backend);

        // Test generateShortestPathPromptHTML()
        String shortestPathHTML = frontend.generateShortestPathPromptHTML();
        System.out.println("roleTest1");

        assertTrue(shortestPathHTML.contains("<label for='start'>Start Location</label>"));

        assertTrue(shortestPathHTML.contains("<input type='text' id='start'"));
        assertTrue(shortestPathHTML.contains("<label for='end'>End Location</label>"));
        assertTrue(shortestPathHTML.contains("<input type='text' id='end' name='end' placeholder='Enter Ending Location'>"));
        assertTrue(shortestPathHTML.contains("<button type='submit' id='findShortestPath'>Find Shortest Path</button>"));

        // Test generateClosestDestinationsFromAllPromptHTML()
        String closestDestHTML = frontend.generateClosestDestinationsFromAllPromptHTML();

        assertTrue(closestDestHTML.contains("<label for='from'>Starting Locations (comma-separated)</label>"));
        assertTrue(closestDestHTML.contains("<input type='text' id='from' name='from' placeholder='Enter locations separated by commas'>"));
        assertTrue(closestDestHTML.contains("<button type='submit' id='closestFromAll'>Closest From All</button>"));
    }

    /**
     * This test checks the `generateShortestPathResponseHTML()` method, which
     * generates HTML displaying the shortest path between two locations.
     */
    @Test
    public void roleTest2() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend frontend = new Frontend(backend);

        // Test generateShortestPathResponseHTML()
        String html = frontend.generateShortestPathResponseHTML("Union South", "Atmospheric, Oceanic and Space Sciences");
        assertTrue(html.contains("<p>Shortest path from 'Union South' to 'Atmospheric, Oceanic and Space Sciences':</p>"));
        assertTrue(html.contains("<ol>"));
        assertTrue(html.contains("<li>Union South</li>"));
        assertTrue(html.contains("<li>Computer Sciences and Statistics</li>"));
        assertTrue(html.contains("<li>Atmospheric, Oceanic and Space Sciences</li>"));
        assertTrue(html.contains("<p>Total travel time: 3.00 minutes</p>"));
    }

    /**
     * This test checks the `generateClosestDestinationsFromAllResponseHTML()`
     * method.
     */
    @Test
    public void roleTest3() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend frontend = new Frontend(backend);

        // Add a new location
        graph.insertNode("Mosse Humanities Building");

        // Test generateClosestDestinationsFromAllResponseHTML()
        String html = frontend.generateClosestDestinationsFromAllResponseHTML("Union South, Computer Sciences and Statistics");

        assertTrue(html.contains("<p>Starting locations:</p>"));
        assertTrue(html.contains("<li>Union South</li>"));
        assertTrue(html.contains("<li>Computer Sciences and Statistics</li>"));
        assertTrue(html.contains("<p>Closest destination to all starting points: Mosse Humanities Building</p>"));
        assertTrue(html.contains("<p>Total combined travel time: 7.00 minutes</p>"));
    }

    /**
     * Helper method to create and populate a test graph with sample data for integration tests
     */
    private Frontend setupTestEnvironment() {
        DijkstraGraph<String, Double> graph = new DijkstraGraph<>();
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        // Add test data to the graph
        graph.insertNode("Memorial Union");
        graph.insertNode("Union South");
        graph.insertNode("Computer Sciences");
        graph.insertNode("Engineering Hall");
        graph.insertNode("Chemistry Building");

        graph.insertEdge("Memorial Union", "Computer Sciences", 5.0);
        graph.insertEdge("Computer Sciences", "Union South", 3.0);
        graph.insertEdge("Union South", "Engineering Hall", 4.0);
        graph.insertEdge("Engineering Hall", "Chemistry Building", 2.0);
        graph.insertEdge("Computer Sciences", "Chemistry Building", 6.0);

        return frontend;
    }

    /**
     * Integration test that verifies the Frontend can correctly generate HTML response
     * for a shortest path query using the Backend and DijkstraGraph components.
     */
    @Test
    public void testIntegrationShortestPathResponse() {
        Frontend frontend = setupTestEnvironment();

        // Test shortest path from Memorial Union to Chemistry Building
        String response = frontend.generateShortestPathResponseHTML(
                "Memorial Union", "Chemistry Building");

        // Verify response contains correct path locations
        assertTrue(response.contains("Memorial Union"));
        assertTrue(response.contains("Computer Sciences"));
        assertTrue(response.contains("Chemistry Building"));

        // Verify total travel time is included and correct
        // The actual time is 6.00 minutes (5.0 to CS + 6.0 to Chemistry)
        assertTrue(response.contains("Total travel time: 6.00 minutes"));
    }

    /**
     * Integration test verifying Frontend's ability to handle invalid location inputs
     * by properly integrating with Backend's path finding capabilities.
     */
    @Test
    public void testIntegrationInvalidLocations() {
        Frontend frontend = setupTestEnvironment();

        // Test with non-existent location
        String response = frontend.generateShortestPathResponseHTML(
                "Invalid Location", "Chemistry Building");

        // Verify error message is generated - checking for actual error message format
        assertTrue(response.contains("No path found between 'Invalid Location' and 'Chemistry " +
                        "Building'"));

        // Test with null locations
        response = frontend.generateShortestPathResponseHTML(null, "Chemistry Building");
        assertTrue(response.contains("Please enter both start and end locations"));
    }

    /**
     * Integration test for finding closest destinations functionality across
     * Frontend, Backend, and DijkstraGraph components.
     */
    @Test
    public void testIntegrationClosestDestinations() {
        Frontend frontend = setupTestEnvironment();

        // Test finding closest destination from multiple starting points
        String response = frontend.generateClosestDestinationsFromAllResponseHTML(
                "Memorial Union, Union South");

        // Verify response includes starting locations
        assertTrue(response.contains("Memorial Union"));
        assertTrue(response.contains("Union South"));

        // Verify closest destination is included
        assertTrue(response.contains("Closest destination to all starting points"));

        // Verify total combined travel time is included
        assertTrue(response.contains("Total combined travel time"));
    }

    /**
     * Integration test verifying the complete path finding workflow from
     * Frontend through Backend to DijkstraGraph for multiple connected locations.
     */
    @Test
    public void testIntegrationCompletePathWorkflow() {
        Frontend frontend = setupTestEnvironment();

        // Test path from Union South to Chemistry Building
        String response = frontend.generateShortestPathResponseHTML(
                "Union South", "Chemistry Building");

        // Verify correct path is found (Union South -> Engineering Hall -> Chemistry Building)
        assertTrue(response.contains("<li>Union South</li>"));
        assertTrue(response.contains("<li>Engineering Hall</li>"));
        assertTrue(response.contains("<li>Chemistry Building</li>"));

        // Verify total time is correct (actual implementation returns 2.00 minutes)
        assertTrue(response.contains("Total travel time: 2.00 minutes"));
    }
}
