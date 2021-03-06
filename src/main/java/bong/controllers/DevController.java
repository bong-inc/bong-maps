package bong.controllers;

import bong.canvas.City;
import bong.canvas.MapCanvas;
import bong.canvas.PointOfInterest;
import bong.canvas.Type;
import bong.routeFinding.Instruction;
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

    @FXML private Button zoomIn;
    @FXML private Button zoomOut;
    @FXML private FlowPane filterTypes;
    @FXML private Button selectall;
    @FXML private Button deselectall;
    @FXML private CheckBox smartTraceToggle;
    @FXML private CheckBox colorToggle;
    @FXML private CheckBox citiesToggle;
    @FXML private CheckBox dependentDrawToggle;
    @FXML private TextField startPoint;
    @FXML private Button showDijkstra;
    @FXML private TextField endPoint;
    @FXML private Button findRoute;
    @FXML private ComboBox<String> vehicle;
    @FXML private Button clearRoute;
    @FXML private CheckBox shortestRoute;
    @FXML private Button routeDescription;
    @FXML private Button printPOI;
    @FXML private CheckBox fullscreenRange;
    @FXML private CheckBox drawBoundingBox;
    @FXML private CheckBox showClosestNode;
    @FXML private CheckBox drawBound;
    @FXML private CheckBox drawPrettyCitynames;
    @FXML private CheckBox showFoundRoadNode;
    @FXML private CheckBox useBidirectional;
    @FXML private CheckBox useAStar;

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
            try {
                canvas.getRouteController().setDijkstra(Long.parseLong(startPoint.getText()), Long.parseLong(endPoint.getText()), vehicle.getValue(), shortestRoute.isSelected(), useBidirectional.isSelected(), useAStar.isSelected());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        clearRoute.setOnAction(e -> {
            canvas.getRouteController().clearRoute();
        });

        shortestRoute.setSelected(true);

        routeDescription.setOnAction(e -> {
            for (Instruction instruction : canvas.getRouteController().getInstructions()) {
                System.out.println(instruction.getInstruction());
            }
        });

        printPOI.setOnAction(e -> {
            for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                System.out.println(poi.toString());
            }
        });

        fullscreenRange.selectedProperty().set(canvas.getRenderFullScreen());
        fullscreenRange.setOnAction(e -> {
            canvas.setRenderFullScreen(fullscreenRange.isSelected());
            canvas.repaint();
        });

        drawBoundingBox.selectedProperty().set(MapCanvas.drawBoundingBox);
        drawBoundingBox.setOnAction(e -> {
            MapCanvas.drawBoundingBox = drawBoundingBox.isSelected();
            canvas.repaint();
        });

        showClosestNode.setSelected(false);
        showClosestNode.setOnAction(e -> {
            canvas.setShowStreetNodeCloseToMouse(showClosestNode.isSelected());
        });

        drawBound.setSelected(canvas.getDrawBound());
        drawBound.setOnAction(e -> {
            canvas.setDrawBound(drawBound.isSelected());
        });
        
        drawPrettyCitynames.setSelected(City.getDrawPrettyCitynames());
        drawPrettyCitynames.setOnAction(e -> {
            City.setDrawPrettyCitynames(drawPrettyCitynames.isSelected());
        });

        showFoundRoadNode.setSelected(canvas.isShowRoadNodes());
        showFoundRoadNode.setOnAction(e -> {
            canvas.setShowRoadNodes(showFoundRoadNode.isSelected());
        });

        useBidirectional.setSelected(true);
        useAStar.setSelected(true);
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