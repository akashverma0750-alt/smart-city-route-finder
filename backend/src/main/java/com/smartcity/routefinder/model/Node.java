package com.smartcity.routefinder.model;

import java.util.Objects;

/**
 * Represents a city or vertex in the Smart City Route Finder network.
 * Each Node encapsulates a unique city name and provides consistent
 * equality, hashing, and natural ordering for graph operations.
 */
public class Node implements Comparable<Node> {
    private final String name;

    public Node(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return name.equalsIgnoreCase(node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public int compareTo(Node other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}

