package com.example.vienneubahnassignment;

import java.util.*;

public class Graph {
    private final Map<String, Station> stations = new HashMap<>();

    public void addStation(Station station) {
        stations.put(station.getName(), station);
    }

    public void addEdge(String from, String to, double distance, String line) {
        // creates a bi-directional connection between the two stations
        Station source = stations.get(from);
        Station dest = stations.get(to);

        // makes sure both stations exist
        if (source == null || dest == null) return;

        // adds a connection from the source (start) to destination (end)
        source.addConnection(new Edge(dest, distance, line));

        // track what line the station is apart of
        source.addLine(line);
        dest.addLine(line);
    }

    public Station getStation(String name) {
        return stations.get(name);
    }

    public Set<String> getStationNames() {
        return stations.keySet();
    }

    public Collection<Station> getAllStations() {
        return stations.values();
    }
}
