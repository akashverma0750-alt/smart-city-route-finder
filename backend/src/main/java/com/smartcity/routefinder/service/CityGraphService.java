package com.smartcity.routefinder.service;

import com.smartcity.routefinder.dto.*;
import com.smartcity.routefinder.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityGraphService {

    private final CityGraph graph = new CityGraph();

    @PostConstruct
    public void init() {
        graph.initializeDefaultNetwork();
    }

    public List<CityDto> getAllCities() {
        List<CityDto> cityDtos = new ArrayList<>();
        for (Node node : graph.getAllNodes()) {
            List<Edge> neighbors = graph.getNeighbors(node);
            List<String> connected = neighbors.stream()
                    .map(e -> e.getDestination().getName())
                    .collect(Collectors.toList());
            cityDtos.add(new CityDto(node.getName(), neighbors.size(), connected));
        }
        return cityDtos;
    }

    public List<RoadResponse> getAllRoads() {
        List<CityGraph.UniqueRoad> uniqueRoads = graph.getAllUniqueRoads();
        List<RoadResponse> responses = new ArrayList<>();
        for (CityGraph.UniqueRoad road : uniqueRoads) {
            responses.add(new RoadResponse(road.getCityA(), road.getCityB(), road.getDistance()));
        }
        return responses;
    }

    public RouteResponse findShortestRoute(String sourceCity, String destinationCity) {
        if (sourceCity == null || sourceCity.trim().isEmpty() ||
                destinationCity == null || destinationCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Source and destination city names must be provided.");
        }

        Node srcNode = graph.getNode(sourceCity);
        Node dstNode = graph.getNode(destinationCity);

        if (srcNode == null) {
            throw new NoSuchElementException("Source city '" + sourceCity + "' is not registered in the network.");
        }
        if (dstNode == null) {
            throw new NoSuchElementException("Destination city '" + destinationCity + "' is not registered in the network.");
        }

        RouteAlgorithms.DijkstraResult result = RouteAlgorithms.findShortestRoute(graph, srcNode, dstNode);

        RouteResponse response = new RouteResponse();
        response.setSource(srcNode.getName());
        response.setDestination(dstNode.getName());
        response.setReachable(result.isReachable());
        response.setDisclaimer(CityGraph.DEMO_DATA_NOTICE);

        if (!result.isReachable()) {
            response.setTotalDistance(-1);
            response.setNumberOfRoads(0);
            response.setNumberOfStops(0);
            response.setPath(Collections.emptyList());
            response.setLegs(Collections.emptyList());
            response.setMessage("No reachable route exists between " + srcNode.getName() + " and " + dstNode.getName() + ".");
            return response;
        }

        if (srcNode.equals(dstNode)) {
            response.setTotalDistance(0);
            response.setNumberOfRoads(0);
            response.setNumberOfStops(0);
            response.setPath(List.of(srcNode.getName()));
            response.setLegs(Collections.emptyList());
            response.setMessage("Source and destination are identical (" + srcNode.getName() + "). Total distance is 0 km.");
            return response;
        }

        response.setTotalDistance(result.getTotalDistance());
        response.setPath(result.getPath().stream().map(Node::getName).collect(Collectors.toList()));
        response.setNumberOfRoads(result.getPath().size() - 1);
        response.setNumberOfStops(Math.max(0, result.getPath().size() - 2));

        List<RouteResponse.LegDetail> legs = new ArrayList<>();
        List<Node> path = result.getPath();
        List<Integer> distances = result.getLegDistances();
        int cumulative = 0;

        for (int i = 0; i < distances.size(); i++) {
            cumulative += distances.get(i);
            legs.add(new RouteResponse.LegDetail(
                    path.get(i).getName(),
                    path.get(i + 1).getName(),
                    distances.get(i),
                    cumulative
            ));
        }

        response.setLegs(legs);
        response.setMessage("Optimal route calculated successfully using Dijkstra's Algorithm.");
        return response;
    }

    public BfsDfsResponse getBfsTraversal(String startCity) {
        if (startCity == null || startCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Starting city must not be blank.");
        }
        Node startNode = graph.getNode(startCity);
        if (startNode == null) {
            throw new NoSuchElementException("City '" + startCity + "' is not registered in the network.");
        }

        RouteAlgorithms.BFSResult result = RouteAlgorithms.breadthFirstSearch(graph, startNode);

        List<String> order = result.getTraversalOrder().stream()
                .map(Node::getName)
                .collect(Collectors.toList());

        Map<String, Integer> details = new LinkedHashMap<>();
        for (Map.Entry<Node, Integer> entry : result.getHopLevels().entrySet()) {
            details.put(entry.getKey().getName(), entry.getValue());
        }

        return new BfsDfsResponse(
                "BFS",
                startNode.getName(),
                order,
                order.size(),
                details,
                "Breadth-First Search: Explores the graph radially level-by-level using a FIFO Queue."
        );
    }

    public BfsDfsResponse getDfsTraversal(String startCity) {
        if (startCity == null || startCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Starting city must not be blank.");
        }
        Node startNode = graph.getNode(startCity);
        if (startNode == null) {
            throw new NoSuchElementException("City '" + startCity + "' is not registered in the network.");
        }

        RouteAlgorithms.DFSResult result = RouteAlgorithms.depthFirstSearch(graph, startNode);

        List<String> order = result.getTraversalOrder().stream()
                .map(Node::getName)
                .collect(Collectors.toList());

        Map<String, Integer> details = new LinkedHashMap<>();
        for (Map.Entry<Node, Integer> entry : result.getDiscoveryStep().entrySet()) {
            details.put(entry.getKey().getName(), entry.getValue());
        }

        return new BfsDfsResponse(
                "DFS",
                startNode.getName(),
                order,
                order.size(),
                details,
                "Depth-First Search: Explores deeply along adjacent branches before backtracking using recursion."
        );
    }

    public RoadResponse addRoad(RoadRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Road request body cannot be null.");
        }
        graph.addRoad(request.getCityA(), request.getCityB(), request.getDistance());
        return new RoadResponse(request.getCityA().trim(), request.getCityB().trim(), request.getDistance());
    }

    public RoadResponse updateRoad(RoadRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Road request body cannot be null.");
        }
        graph.updateRoadDistance(request.getCityA(), request.getCityB(), request.getDistance());
        return new RoadResponse(request.getCityA().trim(), request.getCityB().trim(), request.getDistance());
    }

    public CityGraph getRawGraph() {
        return graph;
    }
}

