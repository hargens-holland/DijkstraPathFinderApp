import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.io.BufferedReader;

public class Backend implements BackendInterface{
    private GraphADT<String, Double> graph;

    // constructor
    public Backend(GraphADT<String, Double> graph) {
        this.graph = graph;
    }

    /**
     * Goes through the DOT file clearing all the graph data
     * Then creates a new graph clarifying a node or an edge separated by ->
     * @param filename the path to a dot file to read graph data from
     * @throws IOException
     */
    @Override
    public void loadGraphData(String filename) throws IOException {
        // clear existing graph data
        for (String node : graph.getAllNodes()) {
            graph.removeNode(node);
        }

        int nodesAdded = 0;
        int edgesAdded = 0;

        // open DOT file for reading
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // check line to see if it defines a node or an edge
            if (line.contains("->")) {
                try {
                    // parse edges like "Memorial Union" -> "Science Hall" [seconds=105.8];
                    String[] parts = line.split("->");
                    String source = parts[0].trim().replace("\"", ""); // Remove quotes

                    // Split second part on [ to separate target and weight
                    String[] targetParts = parts[1].split("\\[");
                    String target = targetParts[0].trim().replace("\"", ""); // Remove quotes

                    // Extract the weight value between = and ]
                    String weightStr = targetParts[1];
                    weightStr = weightStr.split("=")[1]; // Get part after =
                    weightStr = weightStr.split("\\]")[0]; // Get part before ]
                    weightStr = weightStr.replace(";", ""); // Remove semicolon
                    double weight = Double.parseDouble(weightStr.trim());

                    // add nodes and edge to graph
                    graph.insertNode(source);
                    graph.insertNode(target);
                    graph.insertEdge(source, target, weight);
                    edgesAdded++;
                } catch (Exception e) {
                    // Skip malformed edge lines
                }
            } else if (!line.isEmpty() && !line.startsWith("//") && !line.startsWith("digraph")) {
                // parse nodes like "Memorial Union";
                String node = line.replace(";", "").trim().replace("\"", "");
                graph.insertNode(node);
                nodesAdded++;
            }
        }
        reader.close();
    }

    /**
     * Retrieves the list of nodes/locations in the graph
     * @return a list of locations in the graph
     */
    @Override
    public List<String> getListOfAllLocations() {
        return new ArrayList<>(graph.getAllNodes());
    }

    /**
     * Finds the nodes/locations on the shortest path between the start and end locations
     * @param startLocation the start location of the path
     * @param endLocation the end location of the path
     * @return
     */
    @Override
    public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
        try {
            return graph.shortestPathData(startLocation, endLocation);
        } catch (NoSuchElementException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Finds times/edge weights on the shortest path between start and end locations
     * @param startLocation the start location of the path
     * @param endLocation the end location of the path
     * @return
     */
    @Override
    public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
        List<Double> times = new ArrayList<>();
        List<String> path = findLocationsOnShortestPath(startLocation, endLocation);

        // calculate edge weights between consecutive nodes in the path
        for (int i = 0; i < path.size() - 1; i++) {
            String source = path.get(i);
            String target = path.get(i + 1);

            try {
                // use getEdge to retrieve weight between source and target nodes
                Double weight = graph.getEdge(source, target).doubleValue();
                times.add(weight);
            } catch (NoSuchElementException e) {
                // skip if edge not found
            }
        }
        return times;
    }

    /**
     * Finds closest destination location from the list of starting locations by minimizing travel cost
     * @param startLocations the list of locations to minimize travel time from
     * @return
     * @throws NoSuchElementException
     */
    @Override
    public String getClosestDestinationFromAll(List<String> startLocations) throws NoSuchElementException {
        String closestDest = null;
        double minCost = Double.MAX_VALUE;

        for (String start : startLocations) {
            for (String destination : graph.getAllNodes()) {
                if (!start.equals(destination)) {
                    try {
                        double cost = graph.shortestPathCost(start, destination);
                        if (cost < minCost) {
                            minCost = cost;
                            closestDest = destination;
                        }
                    } catch (NoSuchElementException e) {
                        // ignore if no path exists between start and destination
                    }
                }
            }
        }

        if (closestDest == null) {
            throw new NoSuchElementException("No reachable destination found.");
        }
        return closestDest;
    }
}
