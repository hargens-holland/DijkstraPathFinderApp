
// === CS400 File Header Information ===
// Name: Holland Hargens
// Email: hhargens@wisc.edu
// Group and Team: P2.1814
// Group TA: <name of your group's ta>
// Lecturer: Gary
// Notes to Grader:

import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;



/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new HashtableMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // Verify start and end nodes exist
        if (!containsNode(start) || !containsNode(end)) {
            throw new NoSuchElementException("Start or end node not found in graph");
        }

        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        HashtableMap<NodeType, Double> visited = new HashtableMap<>();

        // Get the start node from the graph
        Node startNode = nodes.get(start);

        // Add initial SearchNode to queue
        pq.add(new SearchNode(startNode, 0, null));

        // Loop until pq is empty
        while (!pq.isEmpty()) {
            SearchNode current = pq.poll();
            NodeType currentData = current.node.data;

            // If end node, return current and end program
            if (currentData.equals(end)) {
                return current;
            }

            // Skip if we've found a better path to this node already
            if (visited.containsKey(currentData) &&
                    visited.get(currentData) < current.cost) {
                continue;
            }
            visited.put(currentData, current.cost);

            // Explore all edges leaving current node
            for (Edge edge : current.node.edgesLeaving) {
                NodeType nextData = edge.successor.data;
                if (visited.containsKey(nextData)) {
                    continue;
                }

                // Calculate new cost to reach successor
                double newCost = current.cost + edge.data.doubleValue();

                // create successor and add to queue
                SearchNode successor = new SearchNode(edge.successor, newCost, current);
                pq.add(successor);
            }
        }
        // If reaches here, no path exists
        throw new NoSuchElementException(
                "No path found between " + start.toString() + " and " + end.toString());
    }


    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // Find the shortest path
        SearchNode endNode = computeShortestPath(start, end);
        LinkedList<NodeType> path = new LinkedList<>();

        // Work backwards from end node adding each node's data to front of list
        SearchNode current = endNode;
        while (current != null) {
            path.addFirst(current.node.data);
            current = current.predecessor;
        }

        return path;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        return computeShortestPath(start, end).cost;

    }

    private DijkstraGraph<String, Integer> graph;

    @BeforeEach
    public void createGraph() {
        graph = new DijkstraGraph<>();

        // Add some nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertNode("I");
        graph.insertNode("L");
        graph.insertNode("M");

        // Add edges
        graph.insertEdge("A", "B", 1);
        graph.insertEdge("A", "M", 5);
        graph.insertEdge("A", "H", 7);

        graph.insertEdge("B", "M", 3);

        graph.insertEdge("I", "D", 1);
        graph.insertEdge("I", "H", 2);

        graph.insertEdge("D", "A", 7);
        graph.insertEdge("D", "F", 4);
        graph.insertEdge("D", "G", 2);

        graph.insertEdge("M", "E", 3);
        graph.insertEdge("M", "I", 4);

        graph.insertEdge("F", "G", 9);

        graph.insertEdge("G", "H", 9);
        graph.insertEdge("G", "L", 7);
        graph.insertEdge("G", "A", 4);

        graph.insertEdge("H", "B", 6);
        graph.insertEdge("H", "L", 2);
        graph.insertEdge("H", "I", 2);
    }

    /**
     * Test case using an example traced through in lecture:
     * Finding shortest path from D to I
     */
    @Test
    public void testLectureExampleShortestPath() {
        // Expected path: A -> B -> C -> E (total cost: 5)
        List<String> path = graph.shortestPathData("D", "I");
        assertEquals(List.of("D", "G", "H", "I"), path);
        assertEquals(13.0, graph.shortestPathCost("D", "I"));
    }

    /**
     * Test case using the same graph but different start/end nodes:
     * Finding shortest path from B to L
     */
    @Test
    public void testDifferentPathSameGraph() {
        // Expected path: A -> B -> D (total cost: 5)
        List<String> path = graph.shortestPathData("B", "L");
        assertEquals(List.of("B", "M", "I", "H", "L"), path);
        assertEquals(11.0, graph.shortestPathCost("B", "L"));
    }

    /**
     * Test case for when no path exists between nodes
     */
    @Test
    public void testNoPathExists() {
        // Should throw exception when no path exists from A to F
        assertThrows(NoSuchElementException.class, () -> {
            graph.shortestPathData("E", "G");
        });

        assertThrows(NoSuchElementException.class, () -> {
            graph.shortestPathCost("L", "F");
        });
    }

    /**
     * Additional test: verify path with multiple possible routes
     */
    @Test
    public void testMultiplePathOptions() {


        // Should still choose A -> B -> D (cost 5) over A -> D (cost 10)
        List<String> path = graph.shortestPathData("A", "D");
        assertEquals(List.of("A", "B", "M", "I", "D"), path);
        assertEquals(9.0, graph.shortestPathCost("A", "D"));
    }

}

