import java.util.*;

/**
 * Manages the city network using an Adjacency List graph representation.
 * <p>
 * Supported features:
 * <ul>
 *   <li>Node/city registration with case-insensitive lookup</li>
 *   <li>Bidirectional weighted road management</li>
 *   <li>Duplicate edge prevention and validation</li>
 *   <li>Adjacency list queries for traversal and pathfinding</li>
 * </ul>
 */
public class CityGraph {
    public static final String DEMO_DATA_NOTICE =
            "[Note: Distances are simulation/demonstration values for academic modeling and not verified live GPS distances.]";

    // LinkedHashMap preserves city registration order for predictable display
    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final Map<Node, List<Edge>> adjacencyList = new LinkedHashMap<>();

    /**
     * Constructs an empty CityGraph.
     */
    public CityGraph() {
    }

    /**
     * Adds a city to the graph if it is not already present.
     *
     * @param cityName The name of the city to add.
     * @return True if the city was added, false if it already exists or was invalid.
     */
    public boolean addCity(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return false;
        }
        String key = cityName.trim().toLowerCase();
        if (nodes.containsKey(key)) {
            return false;
        }
        Node node = new Node(cityName.trim());
        nodes.put(key, node);
        adjacencyList.put(node, new ArrayList<>());
        return true;
    }

    /**
     * Checks if a city exists in the graph (case-insensitive).
     *
     * @param cityName The name of the city.
     * @return True if the city exists.
     */
    public boolean hasCity(String cityName) {
        if (cityName == null) return false;
        return nodes.containsKey(cityName.trim().toLowerCase());
    }

    /**
     * Retrieves the Node instance for a given city name (case-insensitive).
     *
     * @param cityName The name of the city.
     * @return The Node instance, or null if not found.
     */
    public Node getNode(String cityName) {
        if (cityName == null) return null;
        return nodes.get(cityName.trim().toLowerCase());
    }

    /**
     * Returns an unmodifiable list of all registered city nodes in insertion order.
     *
     * @return List of Node objects.
     */
    public List<Node> getAllNodes() {
        return Collections.unmodifiableList(new ArrayList<>(nodes.values()));
    }

    /**
     * Total number of cities in the graph.
     *
     * @return City count.
     */
    public int getCityCount() {
        return nodes.size();
    }

    /**
     * Returns the outgoing edges (roads) for a given node.
     *
     * @param node The node whose neighbors are requested.
     * @return List of Edge objects, or empty list if node has no neighbors or does not exist.
     */
    public List<Edge> getNeighbors(Node node) {
        if (node == null || !adjacencyList.containsKey(node)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(adjacencyList.get(node));
    }

    /**
     * Checks whether a road directly connects two cities.
     *
     * @param cityA First city.
     * @param cityB Second city.
     * @return True if a direct road exists.
     */
    public boolean roadExists(String cityA, String cityB) {
        Node nodeA = getNode(cityA);
        Node nodeB = getNode(cityB);
        if (nodeA == null || nodeB == null) return false;

        List<Edge> edges = adjacencyList.get(nodeA);
        if (edges == null) return false;

        for (Edge edge : edges) {
            if (edge.getDestination().equals(nodeB)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a bidirectional road between two existing cities.
     *
     * @param cityA    First city.
     * @param cityB    Second city.
     * @param distance Distance in kilometres (must be > 0).
     * @return True if the road was added successfully.
     * @throws IllegalArgumentException If cities do not exist, are identical, or distance is invalid.
     * @throws IllegalStateException    If a road already exists between the two cities.
     */
    public boolean addRoad(String cityA, String cityB, int distance) {
        Node nodeA = getNode(cityA);
        Node nodeB = getNode(cityB);

        if (nodeA == null) {
            throw new IllegalArgumentException("City '" + cityA + "' does not exist in the network.");
        }
        if (nodeB == null) {
            throw new IllegalArgumentException("City '" + cityB + "' does not exist in the network.");
        }
        if (nodeA.equals(nodeB)) {
            throw new IllegalArgumentException("Cannot add a road from a city to itself (" + nodeA.getName() + ").");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("Road distance must be a positive integer greater than 0 km.");
        }
        if (roadExists(cityA, cityB)) {
            throw new IllegalStateException("A direct road already exists between " +
                    nodeA.getName() + " and " + nodeB.getName() + ". Use option 7 to update distance.");
        }

        // Add edge in both directions for an undirected road
        adjacencyList.get(nodeA).add(new Edge(nodeB, distance));
        adjacencyList.get(nodeB).add(new Edge(nodeA, distance));
        return true;
    }

    /**
     * Updates the distance of an existing bidirectional road.
     *
     * @param cityA       First city.
     * @param cityB       Second city.
     * @param newDistance New distance in kilometres (must be > 0).
     * @return True if the update succeeded.
     * @throws IllegalArgumentException If cities do not exist, are identical, or distance is invalid.
     * @throws IllegalStateException    If no direct road exists between the two cities.
     */
    public boolean updateRoadDistance(String cityA, String cityB, int newDistance) {
        Node nodeA = getNode(cityA);
        Node nodeB = getNode(cityB);

        if (nodeA == null) {
            throw new IllegalArgumentException("City '" + cityA + "' does not exist in the network.");
        }
        if (nodeB == null) {
            throw new IllegalArgumentException("City '" + cityB + "' does not exist in the network.");
        }
        if (nodeA.equals(nodeB)) {
            throw new IllegalArgumentException("Self-referential roads are not supported.");
        }
        if (newDistance <= 0) {
            throw new IllegalArgumentException("New road distance must be greater than 0 km.");
        }
        if (!roadExists(cityA, cityB)) {
            throw new IllegalStateException("No direct road currently exists between " +
                    nodeA.getName() + " and " + nodeB.getName() + ".");
        }

        // Update direction A -> B
        for (Edge edge : adjacencyList.get(nodeA)) {
            if (edge.getDestination().equals(nodeB)) {
                edge.setDistance(newDistance);
                break;
            }
        }

        // Update direction B -> A
        for (Edge edge : adjacencyList.get(nodeB)) {
            if (edge.getDestination().equals(nodeA)) {
                edge.setDistance(newDistance);
                break;
            }
        }

        return true;
    }

    /**
     * Record representing a unique bidirectional road for display purposes.
     */
    public static class UniqueRoad {
        private final String cityA;
        private final String cityB;
        private final int distance;

        public UniqueRoad(String cityA, String cityB, int distance) {
            this.cityA = cityA;
            this.cityB = cityB;
            this.distance = distance;
        }

        public String getCityA() { return cityA; }
        public String getCityB() { return cityB; }
        public int getDistance() { return distance; }

        @Override
        public String toString() {
            return String.format("%-12s <===> %-12s : %4d km", cityA, cityB, distance);
        }
    }

    /**
     * Returns a list of all unique bidirectional roads currently in the graph.
     * Each road is listed only once regardless of traversal direction.
     *
     * @return List of UniqueRoad objects.
     */
    public List<UniqueRoad> getAllUniqueRoads() {
        List<UniqueRoad> roads = new ArrayList<>();
        Set<String> seenEdges = new HashSet<>();

        for (Map.Entry<Node, List<Edge>> entry : adjacencyList.entrySet()) {
            Node source = entry.getKey();
            for (Edge edge : entry.getValue()) {
                Node dest = edge.getDestination();
                // Create canonical key using alphabetical order to avoid duplicate undirected listings
                String key = source.getName().compareToIgnoreCase(dest.getName()) < 0
                        ? source.getName().toLowerCase() + "---" + dest.getName().toLowerCase()
                        : dest.getName().toLowerCase() + "---" + source.getName().toLowerCase();

                if (!seenEdges.contains(key)) {
                    seenEdges.add(key);
                    roads.add(new UniqueRoad(source.getName(), dest.getName(), edge.getDistance()));
                }
            }
        }
        return roads;
    }

    /**
     * Initializes the default network with the 8 required sample cities and realistic
     * demonstration road distances.
     */
    public void initializeDefaultNetwork() {
        // 1. Register required sample cities
        String[] cities = {
                "Lucknow", "Kanpur", "Bareilly", "Delhi",
                "Agra", "Varanasi", "Prayagraj", "Gorakhpur"
        };
        for (String city : cities) {
            addCity(city);
        }

        // 2. Add sample interconnected roads (demonstration distances in km)
        addRoad("Delhi", "Agra", 210);
        addRoad("Delhi", "Bareilly", 255);
        addRoad("Agra", "Kanpur", 280);
        addRoad("Agra", "Bareilly", 220);
        addRoad("Bareilly", "Lucknow", 250);
        addRoad("Kanpur", "Lucknow", 90);
        addRoad("Kanpur", "Prayagraj", 200);
        addRoad("Lucknow", "Prayagraj", 200);
        addRoad("Lucknow", "Gorakhpur", 270);
        addRoad("Prayagraj", "Varanasi", 125);
        addRoad("Varanasi", "Gorakhpur", 200);
    }
}

