package com.example.vienneubahnassignment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class CSVLoader {

    public static Graph loadGraph(String resourcePath) {
        Graph graph = new Graph();
        Map<String, Station> stationsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(CSVLoader.class.getResourceAsStream(resourcePath)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("Start")) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String from = parts[0].trim();
                String to = parts[1].trim();
                String lineNumber = parts[2].trim();

                Station sFrom = stationsMap.computeIfAbsent(from, k -> new Station(from));
                Station sTo = stationsMap.computeIfAbsent(to, k -> new Station(to));

                sFrom.addLine(lineNumber);
                sTo.addLine(lineNumber);

                graph.addStation(sFrom);
                graph.addStation(sTo);

                double distance = 1.0;

                graph.addEdge(from, to, distance, lineNumber);
            }

        } catch (Exception e) {
            System.err.println("Error loading CSV: " + resourcePath);
            e.printStackTrace();
        }

        return graph;
    }
}
