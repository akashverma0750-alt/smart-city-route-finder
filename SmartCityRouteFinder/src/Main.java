import java.util.*;

/**
 * Main application entry point for the Smart City Route Finder CLI.
 * Provides an interactive menu-driven interface for routing, network exploration,
 * and graph administration using Data Structures and Algorithms.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CityGraph graph = new CityGraph();

    public static void main(String[] args) {
        // Initialize sample network with demonstration data
        graph.initializeDefaultNetwork();

        boolean running = true;
        System.out.println();
        System.out.println("=================================================");
        System.out.println("   WELCOME TO SMART CITY ROUTE FINDER (CLI)     ");
        System.out.println("=================================================");
        System.out.println(CityGraph.DEMO_DATA_NOTICE);

        while (running) {
            displayMenu();
            System.out.print("Enter your choice (1-8): ");

            if (!scanner.hasNextLine()) {
                // Handle end-of-stream (e.g. piped input or EOF)
                System.out.println("\nExiting application. Goodbye!");
                break;
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Invalid input! Please enter a valid menu number between 1 and 8.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleViewAllLocations();
                    break;
                case 2:
                    handleViewAllRoads();
                    break;
                case 3:
                    handleFindShortestRoute();
                    break;
                case 4:
                    handleExploreBFS();
                    break;
                case 5:
                    handleExploreDFS();
                    break;
                case 6:
                    handleAddNewRoad();
                    break;
                case 7:
                    handleUpdateRoadDistance();
                    break;
                case 8:
                    System.out.println();
                    System.out.println("=================================================");
                    System.out.println(" Thank you for using Smart City Route Finder!    ");
                    System.out.println(" Safe travels & happy routing!                   ");
                    System.out.println("=================================================");
                    running = false;
                    break;
                default:
                    printError("Choice " + choice + " is out of range. Please choose between 1 and 8.");
                    break;
            }
        }
    }

    /**
     * Prints the primary application menu conforming to specifications.
     */
    private static void displayMenu() {
        System.out.println();
        System.out.println("=====================================");
        System.out.println("       SMART CITY ROUTE FINDER       ");
        System.out.println("=====================================");
        System.out.println("1. View All Locations");
        System.out.println("2. View All Roads");
        System.out.println("3. Find Shortest Route");
        System.out.println("4. Explore Network Using BFS");
        System.out.println("5. Explore Network Using DFS");
        System.out.println("6. Add New Road");
        System.out.println("7. Update Road Distance");
        System.out.println("8. Exit");
        System.out.println("=====================================");
    }

    /**
     * Feature 1: View all available city locations.
     */
    private static void handleViewAllLocations() {
        List<Node> cities = graph.getAllNodes();
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("               AVAILABLE CITY LOCATIONS           ");
        System.out.println("--------------------------------------------------");
        System.out.println("Total Registered Locations: " + cities.size());
        System.out.println();
        int index = 1;
        for (Node city : cities) {
            int roadCount = graph.getNeighbors(city).size();
            System.out.printf("  %2d. %-15s (Connected to %d %s)%n",
                    index++, city.getName(), roadCount, roadCount == 1 ? "road" : "roads");
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Feature 2: View all roads and their distances.
     */
    private static void handleViewAllRoads() {
        List<CityGraph.UniqueRoad> roads = graph.getAllUniqueRoads();
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
        System.out.println("                     ROAD NETWORK & DISTANCES                    ");
        System.out.println("-----------------------------------------------------------------");
        System.out.println(CityGraph.DEMO_DATA_NOTICE);
        System.out.println();
        System.out.printf("  %-4s %-14s          %-14s %s%n", "#", "City A", "City B", "Distance");
        System.out.println("  ---------------------------------------------------------------");
        int index = 1;
        int totalNetworkKm = 0;
        for (CityGraph.UniqueRoad road : roads) {
            System.out.printf("  %2d.  %-14s <=====> %-14s : %4d km%n",
                    index++, road.getCityA(), road.getCityB(), road.getDistance());
            totalNetworkKm += road.getDistance();
        }
        System.out.println("  ---------------------------------------------------------------");
        System.out.printf("  Total Roads: %d (All bidirectional) | Aggregate Length: %d km%n",
                roads.size(), totalNetworkKm);
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Feature 3: Find shortest route using Dijkstra's Algorithm.
     */
    private static void handleFindShortestRoute() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("         FIND SHORTEST ROUTE (DIJKSTRA)          ");
        System.out.println("--------------------------------------------------");

        Node sourceNode = promptForCity("Enter source city: ");
        if (sourceNode == null) return;

        Node targetNode = promptForCity("Enter destination city: ");
        if (targetNode == null) return;

        RouteAlgorithms.DijkstraResult result =
                RouteAlgorithms.findShortestRoute(graph, sourceNode, targetNode);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println("                     SHORTEST ROUTE RESULT                       ");
        System.out.println("=================================================================");

        if (!result.isReachable()) {
            System.out.println("Source City      : " + sourceNode.getName());
            System.out.println("Destination City : " + targetNode.getName());
            System.out.println("Status           : Unreachable (No connecting path exists)");
            System.out.println("Notice           : These locations are in disconnected components of the network.");
            System.out.println("=================================================================");
            return;
        }

        if (sourceNode.equals(targetNode)) {
            System.out.println("Source and destination are the same location (" + sourceNode.getName() + ").");
            System.out.println("Total Distance : 0 km");
            System.out.println("Complete Path  : " + sourceNode.getName());
            System.out.println("=================================================================");
            return;
        }

        System.out.println("Source City      : " + sourceNode.getName());
        System.out.println("Destination City : " + targetNode.getName());
        System.out.println("Total Distance   : " + result.getTotalDistance() + " km");
        System.out.println("Total Stops      : " + (result.getPath().size() - 2) + " intermediate hop(s)");
        System.out.println("Complete Path    : " + result.formatPath());
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Turn-by-Turn Leg Breakdown:");

        List<Node> path = result.getPath();
        List<Integer> legs = result.getLegDistances();
        int cumulative = 0;
        for (int i = 0; i < legs.size(); i++) {
            cumulative += legs.get(i);
            System.out.printf("  Leg %d: %-12s ➔ %-12s | %4d km (Cumulative: %4d km)%n",
                    (i + 1), path.get(i).getName(), path.get(i + 1).getName(), legs.get(i), cumulative);
        }
        System.out.println("=================================================================");
    }

    /**
     * Feature 4: Explore network using Breadth-First Search (BFS).
     */
    private static void handleExploreBFS() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("       EXPLORE NETWORK VIA BFS (LEVEL-ORDER)     ");
        System.out.println("--------------------------------------------------");

        Node startCity = promptForCity("Enter starting city for BFS exploration: ");
        if (startCity == null) return;

        RouteAlgorithms.BFSResult bfsResult =
                RouteAlgorithms.breadthFirstSearch(graph, startCity);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println("              BFS EXPLORATION RESULT (BREADTH-FIRST)             ");
        System.out.println("=================================================================");
        System.out.println("Start Node     : " + startCity.getName());
        System.out.println("Algorithm Note : Explores nodes radially level-by-level using a FIFO Queue.");
        int count = bfsResult.getTraversalOrder().size();
        System.out.println("Total Visited  : " + count + (count == 1 ? " city" : " cities"));
        System.out.println("-----------------------------------------------------------------");

        StringBuilder pathSb = new StringBuilder();
        for (int i = 0; i < bfsResult.getTraversalOrder().size(); i++) {
            pathSb.append(bfsResult.getTraversalOrder().get(i).getName());
            if (i < bfsResult.getTraversalOrder().size() - 1) {
                pathSb.append(" ➔ ");
            }
        }
        System.out.println("Traversal Order : " + pathSb);
        System.out.println();
        System.out.println("Hops from " + startCity.getName() + " (Level Structure):");

        int currentLevel = -1;
        for (Node node : bfsResult.getTraversalOrder()) {
            int level = bfsResult.getHopLevels().get(node);
            if (level != currentLevel) {
                currentLevel = level;
                System.out.printf("%n  [Level %d (%d hop%s)]: ",
                        currentLevel, currentLevel, currentLevel == 1 ? "" : "s");
            }
            System.out.print(node.getName() + "  ");
        }
        System.out.println();
        System.out.println("=================================================================");
    }

    /**
     * Feature 5: Explore network using Depth-First Search (DFS).
     */
    private static void handleExploreDFS() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("       EXPLORE NETWORK VIA DFS (DEPTH-FIRST)      ");
        System.out.println("--------------------------------------------------");

        Node startCity = promptForCity("Enter starting city for DFS exploration: ");
        if (startCity == null) return;

        RouteAlgorithms.DFSResult dfsResult =
                RouteAlgorithms.depthFirstSearch(graph, startCity);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println("              DFS EXPLORATION RESULT (DEPTH-FIRST)               ");
        System.out.println("=================================================================");
        System.out.println("Start Node     : " + startCity.getName());
        System.out.println("Algorithm Note : Explores deeply along branch edges before backtracking.");
        int count = dfsResult.getTraversalOrder().size();
        System.out.println("Total Visited  : " + count + (count == 1 ? " city" : " cities"));
        System.out.println("-----------------------------------------------------------------");

        StringBuilder pathSb = new StringBuilder();
        for (int i = 0; i < dfsResult.getTraversalOrder().size(); i++) {
            pathSb.append(dfsResult.getTraversalOrder().get(i).getName());
            if (i < dfsResult.getTraversalOrder().size() - 1) {
                pathSb.append(" ➔ ");
            }
        }
        System.out.println("Traversal Order : " + pathSb);
        System.out.println();
        System.out.println("Discovery Step Order:");
        int step = 1;
        for (Node node : dfsResult.getTraversalOrder()) {
            System.out.printf("  Step %d: %s%n", step++, node.getName());
        }
        System.out.println("=================================================================");
    }

    /**
     * Feature 6: Add a new road between locations.
     * Also supports registering new city locations (including disconnected/isolated locations).
     */
    private static void handleAddNewRoad() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("                  ADD NEW ROAD                    ");
        System.out.println("--------------------------------------------------");

        Node cityA = promptForCityOrRegister("Enter first city: ");
        if (cityA == null) return;

        Node cityB = promptForCityOrRegister("Enter second city: ");
        if (cityB == null) return;

        if (cityA.equals(cityB)) {
            printError("Cannot add a road between a city and itself (" + cityA.getName() + ").");
            return;
        }

        if (graph.roadExists(cityA.getName(), cityB.getName())) {
            printError("A road already exists between " + cityA.getName() + " and " + cityB.getName() +
                    ". Use menu option 7 to update its distance.");
            return;
        }

        Integer distance = promptForPositiveDistance("Enter road distance in kilometres: ");
        if (distance == null) return;

        try {
            graph.addRoad(cityA.getName(), cityB.getName(), distance);
            System.out.println();
            printSuccess("Successfully added bidirectional road: " +
                    cityA.getName() + " <=====> " + cityB.getName() + " (" + distance + " km).");
        } catch (Exception e) {
            printError("Failed to add road: " + e.getMessage());
        }
    }

    /**
     * Prompts user for a city name. If not registered, offers the user to register it,
     * with an option to leave it disconnected/isolated for reachability testing.
     */
    private static Node promptForCityOrRegister(String promptMessage) {
        System.out.print(promptMessage);
        if (!scanner.hasNextLine()) {
            return null;
        }
        String cityName = scanner.nextLine().trim();

        if (cityName.isEmpty()) {
            printError("City name cannot be blank.");
            return null;
        }

        Node node = graph.getNode(cityName);
        if (node != null) {
            return node;
        }

        // City is not registered: offer registration
        System.out.print("City '" + cityName + "' is not currently registered. Register as a new location? (y/n): ");
        if (!scanner.hasNextLine()) {
            return null;
        }
        String regChoice = scanner.nextLine().trim();
        if (regChoice.equalsIgnoreCase("y") || regChoice.equalsIgnoreCase("yes")) {
            graph.addCity(cityName);
            Node registeredNode = graph.getNode(cityName);

            System.out.print("Connect '" + registeredNode.getName() + "' with a road now? (y/n): ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String connectChoice = scanner.nextLine().trim();
            if (connectChoice.equalsIgnoreCase("n") || connectChoice.equalsIgnoreCase("no")) {
                printSuccess("Registered '" + registeredNode.getName() +
                        "' as an isolated / disconnected location in the network.");
                return null; // Return to menu with new disconnected node in place
            }
            printSuccess("Location '" + registeredNode.getName() + "' registered.");
            return registeredNode;
        } else {
            printError("City '" + cityName + "' is not registered in the network.");
            System.out.println("Available cities: " + getAvailableCitiesList());
            return null;
        }
    }

    /**
     * Feature 7: Update distance of an existing road.
     */
    private static void handleUpdateRoadDistance() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("               UPDATE ROAD DISTANCE               ");
        System.out.println("--------------------------------------------------");

        Node cityA = promptForCity("Enter first city: ");
        if (cityA == null) return;

        Node cityB = promptForCity("Enter second city: ");
        if (cityB == null) return;

        if (cityA.equals(cityB)) {
            printError("Self-referential roads are not allowed.");
            return;
        }

        if (!graph.roadExists(cityA.getName(), cityB.getName())) {
            printError("No direct road exists between " + cityA.getName() + " and " + cityB.getName() +
                    ". Use menu option 6 to add a new road first.");
            return;
        }

        // Find current distance for user feedback
        int currentDistance = -1;
        for (Edge edge : graph.getNeighbors(cityA)) {
            if (edge.getDestination().equals(cityB)) {
                currentDistance = edge.getDistance();
                break;
            }
        }

        System.out.println("Current distance: " + currentDistance + " km");
        Integer newDistance = promptForPositiveDistance("Enter new road distance in kilometres: ");
        if (newDistance == null) return;

        try {
            graph.updateRoadDistance(cityA.getName(), cityB.getName(), newDistance);
            System.out.println();
            printSuccess("Successfully updated road: " + cityA.getName() + " <=====> " +
                    cityB.getName() + " from " + currentDistance + " km to " + newDistance + " km.");
        } catch (Exception e) {
            printError("Failed to update road distance: " + e.getMessage());
        }
    }

    /**
     * Prompts user for a city name with validation and case-insensitive resolution.
     */
    private static Node promptForCity(String promptMessage) {
        System.out.print(promptMessage);
        if (!scanner.hasNextLine()) {
            return null;
        }
        String cityName = scanner.nextLine().trim();

        if (cityName.isEmpty()) {
            printError("City name cannot be blank.");
            return null;
        }

        Node node = graph.getNode(cityName);
        if (node == null) {
            printError("City '" + cityName + "' is not registered in the network.");
            System.out.println("Available cities: " + getAvailableCitiesList());
            return null;
        }
        return node;
    }

    /**
     * Prompts user for a positive integer road distance.
     */
    private static Integer promptForPositiveDistance(String promptMessage) {
        System.out.print(promptMessage);
        if (!scanner.hasNextLine()) {
            return null;
        }
        String input = scanner.nextLine().trim();

        try {
            int distance = Integer.parseInt(input);
            if (distance <= 0) {
                printError("Distance must be a positive number greater than 0 km.");
                return null;
            }
            return distance;
        } catch (NumberFormatException e) {
            printError("Invalid number format! Please enter an integer distance.");
            return null;
        }
    }

    /**
     * Generates a comma-separated string of all registered cities.
     */
    private static String getAvailableCitiesList() {
        StringBuilder sb = new StringBuilder();
        List<Node> nodes = graph.getAllNodes();
        for (int i = 0; i < nodes.size(); i++) {
            sb.append(nodes.get(i).getName());
            if (i < nodes.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static void printError(String message) {
        System.out.println("❌ ERROR: " + message);
    }

    private static void printSuccess(String message) {
        System.out.println("✔ SUCCESS: " + message);
    }
}

