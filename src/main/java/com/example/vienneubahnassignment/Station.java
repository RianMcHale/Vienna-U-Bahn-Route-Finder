package com.example.vienneubahnassignment;

import java.util.*;

public class Station {
    private final String name;
    private final Set<String> lines = new HashSet<>();
    private final List<Edge> connections = new ArrayList<>();
    private double lat = 0;
    private double lon = 0;

    public Station(String name) {
        this.name = name;
    }

    public Station(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public Set<String> getLines() {
        return lines;
    }

    public void addConnection(Edge edge) {
        // adds neighbouring station via a connection
        connections.add(edge);
    }

    public List<Edge> getConnections() {
        // keeps track of which line the station belongs to
        return connections;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return name;
    }
}
