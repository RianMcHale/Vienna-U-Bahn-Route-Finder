package com.example.vienneubahnassignment;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    @FXML private ComboBox<String> startCombo;
    @FXML private ComboBox<String> endCombo;
    @FXML private TextField avoidField;
    @FXML private TextField waypointField;
    @FXML private Spinner<Integer> penaltySpinner;
    @FXML private TextArea resultArea;
    @FXML private Canvas canvas;
    @FXML private VBox lineLegendBox;

    private Graph graph;
    private RouteFinder finder;
    private Image baseMap;

    private static final Map<String, String> LINE_COLORS = Map.of(
            "1", "red",
            "2", "purple",
            "3", "orange",
            "4", "green",
            "6", "brown"
    );

    @FXML
    public void initialize() {
        // loads graph, routing logic via csv and map image
        graph = CSVLoader.loadGraph("/com/example/vienneubahnassignment/vienna_subway.csv");
        finder = new RouteFinder(graph);
        baseMap = new Image(getClass().getResource("/com/example/vienneubahnassignment/uBahnMap.png").toExternalForm());

        // enter selections into the station drop-down tabs
        List<String> sortedStations = new ArrayList<>(graph.getStationNames());
        Collections.sort(sortedStations);

        startCombo.getItems().addAll(sortedStations);
        endCombo.getItems().addAll(sortedStations);
        startCombo.getSelectionModel().selectFirst();
        endCombo.getSelectionModel().selectLast();

        // not working
        penaltySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        populateLineLegend();

        // shows the image on program launch rather than when find route button is pressed
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(baseMap, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void populateLineLegend() {
        // colored labels for each subway line in the legend
        lineLegendBox.getChildren().clear();
        List<String> sortedLines = LINE_COLORS.keySet().stream()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .toList();

        for (String line : sortedLines) {
            Label label = new Label("Line U" + line);
            label.setStyle("-fx-font-weight: bold; -fx-padding: 5; -fx-background-color: " + LINE_COLORS.get(line) + "; -fx-text-fill: white;");
            lineLegendBox.getChildren().add(label);
        }
    }

    @FXML
    public void handleFindRoute() {
        //get user input for start, end, avoid, waypoints and penalty when find route is pressed
        String start = startCombo.getValue();
        String end = endCombo.getValue();
        // shows if no start or end isnt selected
        if (start == null || end == null) {
            resultArea.setText("Please select both start and end stations.");
            return;
        }

        Set<String> avoid = Arrays.stream(avoidField.getText().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        List<String> waypoints = Arrays.stream(waypointField.getText().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        int penalty = penaltySpinner.getValue();


        // builds the route start to end (includin waypoints)
        List<String> fullRoute = new ArrayList<>();
        String current = start;
        for (String waypoint : waypoints) {
            List<String> partial = finder.findShortestDistanceWithPenalty(current, waypoint, avoid, penalty);
            if (!partial.isEmpty()) {
                if (!fullRoute.isEmpty()) partial.remove(0);
                fullRoute.addAll(partial);
                current = waypoint;
            } else {
                resultArea.setText("No route to waypoint: " + waypoint);
                return;
            }
        }

        // final part from the last waypoint entered to the end
        List<String> finalLeg = finder.findShortestDistanceWithPenalty(current, end, avoid, penalty);
        if (!finalLeg.isEmpty()) {
            if (!fullRoute.isEmpty()) finalLeg.remove(0);
            fullRoute.addAll(finalLeg);
        } else {
            resultArea.setText("No route to destination: " + end);
            return;
        }

        // show directions with the line info and transfer info
        List<String> lineSteps = finder.getLineSteps(fullRoute);

        // builds directions for user
        StringBuilder builder = new StringBuilder();
        builder.append("Total Stops: ").append(fullRoute.size()).append("\n\n");

        for (int i = 0; i < fullRoute.size(); i++) {
            String station = fullRoute.get(i);
            builder.append((i + 1)).append(". ").append(station);
            if (i < lineSteps.size()) {
                builder.append(" (U").append(lineSteps.get(i)).append(")");
            }
            builder.append("\n");

            // tells when to change line
            if (i > 0 && i < lineSteps.size()) {
                String prev = lineSteps.get(i - 1);
                String curr = lineSteps.get(i);
                if (!prev.equals(curr)) {
                    builder.append("   ↳ Change to U").append(curr).append("\n");
                }
            }
        }

        resultArea.setText(builder.toString());
        drawRoute(fullRoute);
    }

    // draws route on map ( not working )
    private void drawRoute(List<String> route) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(baseMap, 0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(3);
        gc.setStroke(Color.RED);


        // scales to fit canvas size
        double scaleX = canvas.getWidth() / baseMap.getWidth();
        double scaleY = canvas.getHeight() / baseMap.getHeight();

        // draw lines connecting each station in the route
        for (int i = 0; i < route.size() - 1; i++) {
            Station s1 = graph.getStation(route.get(i));
            Station s2 = graph.getStation(route.get(i + 1));
            if (s1 != null && s2 != null) {
                double x1 = s1.getLon() * scaleX;
                double y1 = s1.getLat() * scaleY;
                double x2 = s2.getLon() * scaleX;
                double y2 = s2.getLat() * scaleY;
                gc.strokeLine(x1, y1, x2, y2);
            }
        }
    }
}
