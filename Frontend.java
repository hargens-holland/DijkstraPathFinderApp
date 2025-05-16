import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Frontend implements FrontendInterface {
    BackendInterface backend;

    public Frontend(BackendInterface backend) {
        if(backend == null) {
            throw new IllegalArgumentException("Backend cannot be null");
        }
        this.backend = backend;
    }

    public String generateShortestPathPromptHTML() {
        return "<h2>Find Shortest Path</h2>\n" +
                "<form action='/' method='get'>\n" +
                "    <label for='start'>Start Location</label>\n" +
                "    <input type='text' id='start' name='start' placeholder='Enter Starting Location'>\n" +
                "    <br><br>\n" +
                "    <label for='end'>End Location</label>\n" +
                "    <input type='text' id='end' name='end' placeholder='Enter Ending Location'>\n" +
                "    <br><br>\n" +
                "    <button type='submit' id='findShortestPath'>Find Shortest Path</button>\n" +
                "</form>";
    }

    public String generateShortestPathResponseHTML(String start, String end) {
        // Handle empty or null inputs
        if (start == null || start.trim().isEmpty() || end == null || end.trim().isEmpty()) {
            return "<p>Please enter both start and end locations.</p>";
        }

        try {
            // Decode the URL-encoded strings to replace '+' with spaces
            start = URLDecoder.decode(start, StandardCharsets.UTF_8);
            end = URLDecoder.decode(end, StandardCharsets.UTF_8);
            
            List<String> path = backend.findLocationsOnShortestPath(start, end);

            if (path == null || path.isEmpty()) {
                return "<p>No path found between '" + start + "' and '" + end + "'. Please verify location names match exactly as they appear in the graph.</p>";
            }

            List<Double> times = backend.findTimesOnShortestPath(start, end);

            StringBuilder html = new StringBuilder();
            html.append("<p>Shortest path from '" + start + "' to '" + end + "':</p>\n");
            html.append("<ol>\n");

            for (String location : path) {
                html.append("    <li>" + location + "</li>\n");
            }

            html.append("</ol>\n");

            double totalTime = times.get(times.size() - 1);
            html.append("<p>Total travel time: " + String.format("%.2f", totalTime) + " minutes</p>\n");

            return html.toString();
        } catch (Exception e) {
            return "<p>Error finding path: " + e.getMessage() + "</p>" +
                    "<p>Please verify that both locations exist in the graph and are spelled exactly as they appear.</p>";
        }
    }

    public String generateClosestDestinationsFromAllPromptHTML() {
        return "<h2>Find Closest Destination</h2>\n" +
                "<form action='/' method='get'>\n" +
                "    <label for='from'>Starting Locations (comma-separated)</label>\n" +
                "    <input type='text' id='from' name='from' placeholder='Enter locations separated by commas'>\n" +
                "    <br><br>\n" +
                "    <button type='submit' id='closestFromAll'>Closest From All</button>\n" +
                "</form>";
    }

    public String generateClosestDestinationsFromAllResponseHTML(String starts) {
        if (starts == null || starts.trim().isEmpty()) {
            return "<p>Please enter at least one starting location.</p>";
        }

        try {
            // Decode the URL-encoded string to replace '+' with spaces
            starts = URLDecoder.decode(starts, StandardCharsets.UTF_8);
            
            String[] locationArray = starts.split(",");
            List<String> startLocations = new ArrayList<>();
            for (String location : locationArray) {
                startLocations.add(location.trim());
            }

            if (startLocations.isEmpty()) {
                return "<p>Error: No starting locations provided.</p>";
            }

            StringBuilder html = new StringBuilder();
            html.append("<p>Starting locations:</p>\n<ul>\n");

            for (String location : startLocations) {
                html.append("    <li>" + location + "</li>\n");
            }
            html.append("</ul>\n");

            String closestDest = backend.getClosestDestinationFromAll(startLocations);

            double totalTime = 0.0;
            for (String start : startLocations) {
                List<Double> times = backend.findTimesOnShortestPath(start, closestDest);
                if (!times.isEmpty()) {
                    totalTime += times.get(times.size() - 1);
                }
            }

            html.append("<p>Closest destination to all starting points: " + closestDest + "</p>\n");
            html.append("<p>Total combined travel time: " + String.format("%.2f", totalTime) + " minutes</p>\n");

            return html.toString();
        } catch (Exception e) {
            return "<p>Error: " + e.getMessage() + "</p>";
        }
    }
}
