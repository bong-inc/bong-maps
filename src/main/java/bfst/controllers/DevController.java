package bfst.controllers;

import bfst.canvas.MapCanvas;
import bfst.canvas.Type;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevController {
    Stage stage;
    MapCanvas canvas;
    List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

    public DevController(Stage devStage, MapCanvas canvas) {
        this.stage = devStage;
        this.canvas = canvas;
    }

    @FXML
    private Button zoomIn;
    @FXML
    private Button zoomOut;
    @FXML
    private FlowPane filterTypes;
    @FXML
    private Button selectall;
    @FXML
    private Button deselectall;
    @FXML
    private CheckBox smartTraceToggle;
    @FXML
    private CheckBox colorToggle;
    @FXML
    private CheckBox citiesToggle;
    @FXML
    private CheckBox dependentDrawToggle;
    @FXML
    private TextField startPoint;
    @FXML
    private Button showDijkstra;
    @FXML
    private TextField endPoint;
    @FXML
    private Button findRoute;
    @FXML
    private ComboBox<String> vehicle;
    @FXML
    private Button clearRoute;
    @FXML
    private CheckBox shortestRoute;
    @FXML
    private Button routeDescription;

    @FXML
    public void initialize() {

        zoomIn.setOnAction(e -> {
            canvas.zoom(2, canvas.getWidth() / 2, canvas.getHeight() / 2);
        });

        zoomOut.setOnAction(e -> {
            canvas.zoom(0.5, canvas.getWidth() / 2, canvas.getHeight() / 2);
        });

        for (Type type : Type.getTypes()) {
            CheckBox c = new CheckBox(type.name());
            c.setUserData(type);
            c.setSelected(true);
            c.setOnAction(e -> {
                updateTypesToBeDrawn();
            });
            filterTypes.getChildren().add(c);
        }

        selectall.setOnAction(e -> {
            for (Node node : filterTypes.getChildren()) {
                CheckBox check = (CheckBox) node;
                check.setSelected(true);
            }
            canvas.setTypesToBeDrawn(Arrays.asList(Type.getTypes()));
        });

        deselectall.setOnAction(e -> {
            for (Node node : filterTypes.getChildren()) {
                CheckBox check = (CheckBox) node;
                check.setSelected(false);
            }
            canvas.setTypesToBeDrawn(new ArrayList<>());
        });

        smartTraceToggle.setSelected(true);
        smartTraceToggle.setOnAction(e -> {
            canvas.setTraceType(smartTraceToggle.isSelected());
        });

        colorToggle.setSelected(true);
        colorToggle.setOnAction(e -> {
            canvas.setUseRegularColors(colorToggle.isSelected());
        });

        citiesToggle.setSelected(true);
        citiesToggle.setOnAction(e -> {
            canvas.setShowCities(citiesToggle.isSelected());
        });

        dependentDrawToggle.setSelected(true);
        dependentDrawToggle.setOnAction(e -> {
            canvas.setUseDependentDraw(dependentDrawToggle.isSelected());
        });

        vehicle.getItems().addAll("Walk", "Bicycle", "Car");
        vehicle.getSelectionModel().selectLast();

        showDijkstra.setOnAction(e -> {
            canvas.showDijkstraTree();
        });

        findRoute.setOnAction(e -> {
            canvas.setDijkstra(Long.parseLong(startPoint.getText()), Long.parseLong(endPoint.getText()), vehicle.getValue(), shortestRoute.isSelected());
        });

        clearRoute.setOnAction(e -> {
            canvas.clearRoute();
        });

        shortestRoute.setSelected(true);

        routeDescription.setOnAction(e -> {
            for (String string : canvas.getDescription()) {
                System.out.println(string);
            }
            /*
            for (String string : canvas.getRouteDescription(canvas.getRoute())) {
                System.out.println(string);
            }*/
        });
    }

    private void updateTypesToBeDrawn() {
        typesToBeDrawn = new ArrayList<>();
        for (Node node : filterTypes.getChildren()) {
            CheckBox check = (CheckBox) node;
            if (check.isSelected()) typesToBeDrawn.add((Type) check.getUserData());
        }
        canvas.setTypesToBeDrawn(typesToBeDrawn);
    }
}