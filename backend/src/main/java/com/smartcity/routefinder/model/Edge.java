package com.smartcity.routefinder.model;

import java.util.Objects;

/**
 * Represents a weighted, directed road connection pointing to a target Node.
 * In CityGraph, bidirectional roads are represented by two corresponding
 * Edge instances in each node's adjacency list.
 */
public class Edge {
    private final Node destination;
    private int distance;

    public Edge(Node destination, int distance) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination node cannot be null.");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("Road distance must be greater than zero.");
        }
        this.destination = destination;
        this.distance = distance;
    }

    public Node getDestination() {
        return destination;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Road distance must be greater than zero.");
        }
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return destination.equals(edge.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination);
    }

    @Override
    public String toString() {
        return "-> " + destination.getName() + " (" + distance + " km)";
    }
}

