package com.smartcity.routefinder.model;

import java.util.*;

/**
 * Implements fundamental graph algorithms for the Smart City Route Finder:
 * <ul>
 *   <li>Dijkstra's Shortest Path Algorithm (using PriorityQueue, HashMap, HashSet)</li>
 *   <li>Breadth-First Search (using FIFO Queue, HashSet, LinkedHashMap)</li>
 *   <li>Depth-First Search (using recursion, HashSet, LinkedHashMap)</li>
 * </ul>
 */
public class RouteAlgorithms {

    /**
     * Encapsulates the results of Dijkstra's algorithm.
     */
    public static class DijkstraResult {
        private final Node source;
        private final Node destination;
        private final List<Node> path;
        private final List<Integer> legDistances;
        private final int totalDistance;
        private final boolean reachable;

        public DijkstraResult(Node source, Node destination, List<Node> path,
                              List<Integer> legDistances, int totalDistance, boolean reachable) {
            this.source = source;
            this.destination = destination;
            this.path = path;
            this.legDistances = legDistances;
            this.totalDistance = totalDistance;
            this.reachable = reachable;
        }

        public Node getSource() { return source; }
        public Node getDestination() { return destination; }
        public List<Node> getPath() { return Collections.unmodifiableList(path); }
        public List<Integer> getLegDistances() { return Collections.unmodifiableList(legDistances); }
        public int getTotalDistance() { return totalDistance; }
        public boolean isReachable() { return reachable; }

        public String formatPath() {
            if (!reachable || path.isEmpty()) {
                return "No route available";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i).getName());
                if (i < path.size() - 1) {
                    sb.append(" ➔ ");
                }
            }
            return sb.toString();
        }
    }

    private static class QueueNode implements Comparable<QueueNode> {
        final Node node;
        final int distance;

        QueueNode(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(QueueNode other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    /**
     * Finds the shortest route between two locations using Dijkstra's Algorithm.
     * Time Complexity: O((V + E) log V)
     * Space Complexity: O(V)
     */
    public static DijkstraResult findShortestRoute(CityGraph graph, Node source, Node target) {
        if (graph == null || source == null || target == null) {
            throw new IllegalArgumentException("Graph, source, and target must not be null.");
        }

        // Special Case: Source and Destination are identical
        if (source.equals(target)) {
            List<Node> singlePath = new ArrayList<>();
            singlePath.add(source);
            return new DijkstraResult(source, target, singlePath, Collections.emptyList(), 0, true);
        }

        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<QueueNode> pq = new PriorityQueue<>();

        // Initialize all vertex distances to infinity
        for (Node node : graph.getAllNodes()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        distances.put(source, 0);
        pq.add(new QueueNode(source, 0));

        while (!pq.isEmpty()) {
            QueueNode current = pq.poll();
            Node u = current.node;

            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            if (u.equals(target)) {
                break;
            }

            int currentDist = current.distance;
            if (currentDist > distances.get(u)) {
                continue;
            }

            for (Edge edge : graph.getNeighbors(u)) {
                Node v = edge.getDestination();
                if (visited.contains(v)) {
                    continue;
                }

                int edgeWeight = edge.getDistance();
                if (currentDist != Integer.MAX_VALUE && currentDist + edgeWeight < distances.get(v)) {
                    int newDist = currentDist + edgeWeight;
                    distances.put(v, newDist);
                    previous.put(v, u);
                    pq.add(new QueueNode(v, newDist));
                }
            }
        }

        int totalDistance = distances.getOrDefault(target, Integer.MAX_VALUE);
        if (totalDistance == Integer.MAX_VALUE) {
            // Unreachable destination
            return new DijkstraResult(source, target, Collections.emptyList(),
                    Collections.emptyList(), -1, false);
        }

        // Reconstruct path
        List<Node> path = new ArrayList<>();
        Node curr = target;
        while (curr != null) {
            path.add(curr);
            curr = previous.get(curr);
        }
        Collections.reverse(path);

        // Calculate individual leg distances
        List<Integer> legDistances = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = path.get(i);
            Node to = path.get(i + 1);
            for (Edge e : graph.getNeighbors(from)) {
                if (e.getDestination().equals(to)) {
                    legDistances.add(e.getDistance());
                    break;
                }
            }
        }

        return new DijkstraResult(source, target, path, legDistances, totalDistance, true);
    }

    public static class BFSResult {
        private final Node startNode;
        private final List<Node> traversalOrder;
        private final Map<Node, Integer> hopLevels;

        public BFSResult(Node startNode, List<Node> traversalOrder, Map<Node, Integer> hopLevels) {
            this.startNode = startNode;
            this.traversalOrder = traversalOrder;
            this.hopLevels = hopLevels;
        }

        public Node getStartNode() { return startNode; }
        public List<Node> getTraversalOrder() { return Collections.unmodifiableList(traversalOrder); }
        public Map<Node, Integer> getHopLevels() { return Collections.unmodifiableMap(hopLevels); }
    }

    /**
     * Breadth-First Search (BFS) for level-order exploration.
     * Time Complexity: O(V + E)
     */
    public static BFSResult breadthFirstSearch(CityGraph graph, Node startNode) {
        if (graph == null || startNode == null) {
            throw new IllegalArgumentException("Graph and startNode cannot be null.");
        }

        List<Node> traversalOrder = new ArrayList<>();
        Map<Node, Integer> hopLevels = new LinkedHashMap<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        visited.add(startNode);
        queue.add(startNode);
        hopLevels.put(startNode, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            traversalOrder.add(current);
            int currentLevel = hopLevels.get(current);

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getDestination();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    hopLevels.put(neighbor, currentLevel + 1);
                    queue.add(neighbor);
                }
            }
        }

        return new BFSResult(startNode, traversalOrder, hopLevels);
    }

    public static class DFSResult {
        private final Node startNode;
        private final List<Node> traversalOrder;
        private final Map<Node, Integer> discoveryStep;

        public DFSResult(Node startNode, List<Node> traversalOrder, Map<Node, Integer> discoveryStep) {
            this.startNode = startNode;
            this.traversalOrder = traversalOrder;
            this.discoveryStep = discoveryStep;
        }

        public Node getStartNode() { return startNode; }
        public List<Node> getTraversalOrder() { return Collections.unmodifiableList(traversalOrder); }
        public Map<Node, Integer> getDiscoveryStep() { return Collections.unmodifiableMap(discoveryStep); }
    }

    /**
     * Depth-First Search (DFS) for branch exploration.
     * Time Complexity: O(V + E)
     */
    public static DFSResult depthFirstSearch(CityGraph graph, Node startNode) {
        if (graph == null || startNode == null) {
            throw new IllegalArgumentException("Graph and startNode cannot be null.");
        }

        List<Node> traversalOrder = new ArrayList<>();
        Map<Node, Integer> discoveryStep = new LinkedHashMap<>();
        Set<Node> visited = new HashSet<>();

        dfsHelper(graph, startNode, visited, traversalOrder, discoveryStep);

        return new DFSResult(startNode, traversalOrder, discoveryStep);
    }

    private static void dfsHelper(CityGraph graph, Node current, Set<Node> visited,
                                  List<Node> traversalOrder, Map<Node, Integer> discoveryStep) {
        visited.add(current);
        traversalOrder.add(current);
        discoveryStep.put(current, traversalOrder.size());

        for (Edge edge : graph.getNeighbors(current)) {
            Node neighbor = edge.getDestination();
            if (!visited.contains(neighbor)) {
                dfsHelper(graph, neighbor, visited, traversalOrder, discoveryStep);
            }
        }
    }
}

