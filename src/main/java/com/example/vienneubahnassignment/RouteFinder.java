package com.example.vienneubahnassignment;

import java.util.*;

public class RouteFinder {
    private final Graph graph;

    public RouteFinder(Graph graph) {
        this.graph = graph;
    }

    public List<String> getLineSteps(List<String> route) {
        // determine which subway line is used between each pair of stations
        List<String> lineSteps = new ArrayList<>();

        for (int i = 0; i < route.size() - 1; i++) {
            Station current = graph.getStation(route.get(i));
            Station next = graph.getStation(route.get(i + 1));

            // find the edge that connects current to next and extract its label
            String line = current.getConnections().stream()
                    .filter(edge -> edge.getDestination().getName().equals(next.getName()))
                    .map(Edge::getLine)
                    .findFirst()
                    .orElse("?");

            lineSteps.add(line);
        }

        return lineSteps;
    }


    private void dfs(String current, String end, Set<String> avoid, Set<String> visited, List<String> path, List<List<String>> results) {
        if (avoid.contains(current) || visited.contains(current)) return;
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            results.add(new ArrayList<>(path));
        } else {
            for (Edge edge : graph.getStation(current).getConnections()) {
                dfs(edge.getDestination().getName(), end, avoid, visited, path, results);
            }
        }

        visited.remove(current);
        path.remove(path.size() - 1);
    }


    public List<String> findShortestDistanceWithPenalty(String start, String end, Set<String> avoid, int penalty) {
        // dijkstras algorithm added with penalty when changing the subway lines
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Map<String, String> prevLine = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        // set all stations to max distance by default
        for (String station : graph.getStationNames()) {
            dist.put(station, Double.MAX_VALUE);
        }
        dist.put(start, 0.0); // distance to start is 0
        pq.add(start);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (avoid.contains(current)) continue; // skips blocked stations
            if (current.equals(end)) break; // stop when destination is reached

            for (Edge edge : graph.getStation(current).getConnections()) {
                String neighbor = edge.getDestination().getName();
                if (avoid.contains(neighbor)) continue;

                double cost = edge.getDistance();
                // apply penalty if line changes from prev stop
                if (prevLine.containsKey(current) && !prevLine.get(current).equals(edge.getLine())) {
                    cost += penalty;
                }

                double newDist = dist.get(current) + cost;
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    prevLine.put(neighbor, edge.getLine());
                    pq.add(neighbor);
                }
            }
        }
        // return final path by tracing backwards from end to start
        return reconstructPath(prev, start, end);
    }

    // help function that rebuilds full route from previous-stations map
    private List<String> reconstructPath(Map<String, String> prev, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        String current = end;
        while (current != null) {
            path.addFirst(current);
            current = prev.get(current);
        }
        // only return the path if it actually starts fro mthe start node
        if (!path.isEmpty() && path.getFirst().equals(start)) {
            return path;
        }
        return Collections.emptyList();
    }
}
