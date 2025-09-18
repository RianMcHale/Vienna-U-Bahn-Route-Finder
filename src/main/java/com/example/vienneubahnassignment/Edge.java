package com.example.vienneubahnassignment;

public class Edge {
    private final Station destination;
    private final double distance;
    private final String line;

    public Edge(Station destination, double distance, String line) {
        this.destination = destination;
        this.distance = distance;
        this.line = line;
    }

    public Station getDestination() { return destination; }

    public double getDistance() { return distance; }

    public String getLine() { return line; }
}
