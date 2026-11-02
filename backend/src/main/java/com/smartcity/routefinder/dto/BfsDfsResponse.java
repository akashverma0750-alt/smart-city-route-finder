package com.smartcity.routefinder.dto;

import java.util.List;
import java.util.Map;

public class BfsDfsResponse {
    private String algorithm;
    private String startCity;
    private List<String> traversalOrder;
    private int totalVisited;
    private Map<String, Integer> details; // hop levels for BFS, discovery steps for DFS
    private String description;

    public BfsDfsResponse() {}

    public BfsDfsResponse(String algorithm, String startCity, List<String> traversalOrder,
                          int totalVisited, Map<String, Integer> details, String description) {
        this.algorithm = algorithm;
        this.startCity = startCity;
        this.traversalOrder = traversalOrder;
        this.totalVisited = totalVisited;
        this.details = details;
        this.description = description;
    }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getStartCity() { return startCity; }
    public void setStartCity(String startCity) { this.startCity = startCity; }

    public List<String> getTraversalOrder() { return traversalOrder; }
    public void setTraversalOrder(List<String> traversalOrder) { this.traversalOrder = traversalOrder; }

    public int getTotalVisited() { return totalVisited; }
    public void setTotalVisited(int totalVisited) { this.totalVisited = totalVisited; }

    public Map<String, Integer> getDetails() { return details; }
    public void setDetails(Map<String, Integer> details) { this.details = details; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

