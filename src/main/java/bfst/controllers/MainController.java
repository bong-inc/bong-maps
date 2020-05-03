package bfst.controllers;

import bfst.App;
import bfst.OSMReader.MercatorProjector;
import bfst.OSMReader.Model;
import bfst.OSMReader.Node;
import bfst.OSMReader.OSMReader;
import bfst.addressparser.Address;
import bfst.canvas.*;
import bfst.exceptions.FileTypeNotSupportedException;
import bfst.routeFinding.Instruction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController {
    private Stage stage;
    public Model model;
    private Point2D lastMouse;
    private ArrayList<Address> tempBest = new ArrayList<>();
    private boolean hasBeenDragged = false;
    private Address destinationAddress;
    private Address startAddress;
    private Address currentAddress;
    private Point2D destinationPoint;
    private Point2D startPoint;
    private Point2D currentPoint;
    private PointsOfInterestController poiController;
    private SearchController searchController;

    private ToggleGroup vehicleGroup = new ToggleGroup();
    @FXML private RadioButton carButton;
    @FXML private RadioButton bikeButton;
    @FXML private RadioButton walkButton;

    private ToggleGroup shortFastGroup = new ToggleGroup();
    @FXML private RadioButton shortButton;
    @FXML private RadioButton fastButton;

    private MapCanvas canvas;
    private boolean shouldPan = true;
    private boolean showStreetOnHover = false;

    public MainController(Stage primaryStage) {
        this.stage = primaryStage;
        new FileController();
        this.poiController = new PointsOfInterestController();
        this.searchController = new SearchController();
    }

    public void setMapBinaryFromPath(String mapName) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bfst/" + mapName + ".bin");
            setModelFromBinary(is);
        } catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("An error occured");
            alert.setContentText("Failed to load map of " + mapName);
            alert.showAndWait();
        }
    } 
    
    public void setDefaultMap(){
        setMapBinaryFromPath("copenhagen");
    }

    @FXML private VBox welcomeOverlay;
    @FXML private VBox mainView;
    @FXML private Button welcomeDenmark;
    @FXML private Button welcomeCopenhagen;
    @FXML private Button welcomeCustom;

    @FXML private StackPane stackPane;
    @FXML private MapCanvasWrapper mapCanvasWrapper;
    @FXML private MenuBar menu;
    @FXML private MenuItem loadClick;
    @FXML private MenuItem loadDefaultMap;
    @FXML private MenuItem loadDenmark;
    @FXML private MenuItem saveAs;
    @FXML private MenuItem devtools;
    @FXML private MenuItem about;
    @FXML private MenuItem help;
    @FXML private TextField searchField;
    @FXML private VBox suggestionsContainer;
    @FXML private Menu myPoints;
    @FXML private HBox pinInfo;
    @FXML private Label pointAddress;
    @FXML private Label pointCoords;
    @FXML private Button POIButton;
    @FXML private Button setAsDestination;
    @FXML private Button setAsStart;
    @FXML private VBox routeInfo;
    @FXML private Label routeDistance;
    @FXML private Label routeTime;
    @FXML private VBox directions;
    @FXML private Menu view;
    @FXML private CheckMenuItem publicTransport;
    @FXML private CheckMenuItem darkMode;
    @FXML private CheckMenuItem hoverToShowStreet;
    @FXML private MenuItem zoomToArea;
    @FXML private Button findRoute;
    @FXML private VBox directionsInfo;
    @FXML private Label startLabel;
    @FXML private Label destinationLabel;
    @FXML private HBox vehicleSelection;
    @FXML private HBox shortestFastestSelection;
    @FXML private Label noRouteFound;
    @FXML private Button cancelRoute;
    @FXML private Button pinInfoClose;
    @FXML private Button swap;
    
    @FXML
    public void initialize() {

        mainView.setDisable(true);
        mainView.setFocusTraversable(false);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
            setDefaultMap();

            poiController.loadPointsOfInterest();
            for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                addItemToMyPoints(poi);
            }
        });

        loadClick.setOnAction((ActionEvent e) -> {
            loadFileOnClick();
        });

        canvas = mapCanvasWrapper.mapCanvas;

        canvas.setOnMousePressed(e -> {
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        canvas.setOnMouseDragged(e -> {
            hasBeenDragged = true;

            if (shouldPan) {
                canvas.pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
                lastMouse = new Point2D(e.getX(), e.getY());
            } else {
                setLinePathForDrawedSquare(e);
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (!hasBeenDragged && shouldPan) {
                placePin();
            }
            if (!shouldPan) {
                Point2D end = new Point2D(e.getX(), e.getY());
                zoomToArea(end);
            }

            shouldPan = true;
            hasBeenDragged = false;
            canvas.setDraggedSquare(null);
        });

        loadDefaultMap.setOnAction(e -> {
            setDefaultMap();
        });

        loadDenmark.setOnAction(e -> {
            setMapBinaryFromPath("denmark");
        });

        welcomeDenmark.setOnAction(e -> {
            setMapBinaryFromPath("denmark");
            closeWelcomeOverlay();
        });

        welcomeCopenhagen.setOnAction(e -> {
            closeWelcomeOverlay();
        });    

        welcomeCustom.setOnAction(e -> {
            if(loadFileOnClick()) {
                closeWelcomeOverlay();
            }
        });

      

        saveAs.setOnAction(this::saveFileOnClick);

        canvas.setOnScroll(e -> {
            double factor = Math.pow(1.004,e.getDeltaY());
            canvas.zoom(factor,e.getX(),e.getY());
        });

        devtools.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Open dev tools?");
            alert.setContentText("Dev tools are only supposed to be used by developers or advanced users");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                openDevTools();
            }
        });

        publicTransport.setSelected(true);
        publicTransport.setOnAction(e -> {
            updateShowPublicTransport(publicTransport.isSelected());
        });

        darkMode.setSelected(false);
        darkMode.setOnAction(e -> {
            canvas.setUseRegularColors(!darkMode.isSelected());
        });

        hoverToShowStreet.setSelected(showStreetOnHover);
        hoverToShowStreet.setOnAction(e -> {
            showStreetOnHover = hoverToShowStreet.isSelected();
            canvas.repaint();
        });

        zoomToArea.setOnAction(e ->  {
            shouldPan = false;
        });

        about.setOnAction(e -> {
            openAbout();
        });

        help.setOnAction(e -> {
            openHelp();
        });

        setAsDestination.setTooltip(setupTooltip("Set as destination"));
        setAsDestination.setOnAction(e -> {
            canvas.getRouteController().clearRoute();
            destinationAddress = currentAddress;
            destinationPoint = currentPoint;
            canvas.setRouteDestination(destinationPoint);
            showDirectionsMenu();
        });

        setAsStart.setTooltip(setupTooltip("Set as start"));
        setAsStart.setOnAction(e -> {
            canvas.getRouteController().clearRoute();
            startAddress = currentAddress;
            startPoint = currentPoint;
            canvas.setRouteOrigin(startPoint);
            showDirectionsMenu();
        });

        pinInfoClose.setOnAction(e -> {
            canvas.nullPin();
            hidePinInfo();
        });

        findRoute.setOnAction(e -> {
            try {
                findRouteFromGivenInputs();
                showDirectionsMenu();
            } catch (Exception ex) {
                routeInfo.setVisible(false);
                routeInfo.setManaged(false);
                noRouteFound.setVisible(true);
                noRouteFound.setManaged(true);
                canvas.getRouteController().clearRoute();
                ex.printStackTrace();
            }
        });

        canvas.setOnMouseMoved(e -> {
            if (showStreetOnHover) {
                canvas.showStreetNearMouse(this, e);
            }

        });

        swap.setOnAction(e -> {
            swapStartAndDestination();
        });

        setRouteOptionButtons();

        searchField.focusedProperty().addListener((obs,oldVal,newVal) -> {
            if (newVal) {
                searchField.setText(searchController.getCurrentQuery());
            }
        });

        searchField.textProperty().addListener((obs,oldVal,newVal) -> {
            hidePinInfo();
            if (searchField.isFocused()) setCurrentQuery(searchField.getText().trim());
            if (searchField.getText().length() == 0) suggestionsContainer.getChildren().clear();
            canvas.nullPin();
        });

        searchField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
            }
            if (event.getCode() == KeyCode.DOWN) {
                if(suggestionsContainer.getChildren().size() > 0) {
                    suggestionsContainer.getChildren().get(0).requestFocus();
                }
                event.consume();
            }
        });

        searchField.setOnAction(e -> {
            if(suggestionsContainer.getChildren().size() > 0) {
                SuggestionButton b = (SuggestionButton) suggestionsContainer.getChildren().get(0);
                Address a = b.getAddress();
                goToAddress(a);
            }
        });
    }

    private Tooltip setupTooltip(String message){
        Tooltip tip = new Tooltip("Set as start");
        tip.setShowDelay(Duration.ZERO);
        return tip;
    }

    private void closeWelcomeOverlay() {
        mainView.setDisable(false);
        mainView.setFocusTraversable(true);
        welcomeOverlay.setVisible(false);
    }

    public void setCurrentQuery(String newQuery){
        searchController.setCurrentQuery(newQuery);
        tempBest = searchController.getBestMatches(newQuery, model.getAddresses(), 5);
        updateSuggestionsContainer();
    }

    public void updateSuggestionsContainer(){
        ArrayList<Address> best = tempBest;
        ArrayList<SuggestionButton> bs = new ArrayList<>();
        for (Address address : best) {
            String addressString = address.toString();

                SuggestionButton b = setUpSuggestionButton(address, addressString);
                bs.add(b);
        }
        suggestionsContainer.getChildren().clear();
        for (SuggestionButton b : bs) suggestionsContainer.getChildren().add(b);
    }

    private SuggestionButton setUpSuggestionButton(Address address, String addressString) {
        SuggestionButton b = new SuggestionButton(address);
        b.setOnAction(e -> {
            goToAddress(b.getAddress());
        });
        b.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                setCurrentQuery(((SuggestionButton) event.getSource()).getAddress().toString());
                searchField.requestFocus();
                searchField.positionCaret(searchField.getText().length());
                event.consume();
            }
            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                searchField.requestFocus();
            }
        });
        b.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Address a = b.getAddress();
                searchField.setText(a.toString());
                peekAddress(a);
            }
        });
        return b;
    }

    public void goToAddress(Address a) {
        setCurrentQuery(a.toString());
        searchField.setText(a.toString());
        searchField.positionCaret(searchField.getText().length());
        canvas.zoomToPoint(1, a.getLon(),  a.getLat());
        canvas.setPin(a.getLon(), a.getLat());
        suggestionsContainer.getChildren().clear();
        showPinMenu();
    }

    private void peekAddress(Address a) {
        searchField.setText(a.toString());
        canvas.zoomToPoint(1, a.getLon(),  a.getLat());
        canvas.setPin(a.getLon(), a.getLat());
        showPinMenu();
    }

    private void setRouteOptionButtons() {
        carButton.setToggleGroup(vehicleGroup);
        carButton.setUserData("Car");
        bikeButton.setToggleGroup(vehicleGroup);
        bikeButton.setUserData("Bicycle");
        walkButton.setToggleGroup(vehicleGroup);
        walkButton.setUserData("Walk");
        carButton.setSelected(true);

        bikeButton.setOnAction(e -> {
            disableShortFastChoice();
        });
        walkButton.setOnAction(e -> {
            disableShortFastChoice();
        });
        carButton.setOnAction(e -> {
            shortButton.setDisable(false);
            fastButton.setDisable(false);
        });

        shortButton.setToggleGroup(shortFastGroup);
        fastButton.setToggleGroup(shortFastGroup);
        shortButton.setSelected(true);

        cancelRoute.setOnAction(e -> {
            canvas.getRouteController().clearRoute();
            startAddress = null;
            destinationAddress = null;
            destinationPoint = null;
            startPoint = null;
            canvas.clearOriginDestination();
            directionsInfo.setVisible(false);
        });
    }

    private void findRouteFromGivenInputs() throws Exception {
        RadioButton selectedVehicleButton = (RadioButton) vehicleGroup.getSelectedToggle();
        String vehicle = (String) selectedVehicleButton.getUserData();
        RadioButton selectedShortFastButton = (RadioButton) shortFastGroup.getSelectedToggle();
        boolean shortestRoute = selectedShortFastButton.getText().equals("Shortest");

        Node startNode = ((Node) model.getRoadKDTree().nearestNeighborForEdges(startPoint, vehicle));
        Node destinationNode = ((Node) model.getRoadKDTree().nearestNeighborForEdges(destinationPoint, vehicle));
        canvas.setStartDestPoint(startNode, destinationNode);

        long startRoadId = startNode.getAsLong();
        long destinationRoadId = destinationNode.getAsLong();

        canvas.getRouteController().setDijkstra(startRoadId, destinationRoadId, vehicle, shortestRoute, true, true);

    }

    private void openHelp() {
        try {
            Stage helpStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/help.fxml"));
            Parent root = fxmlLoader.load();
            helpStage.setTitle("Help");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("bfst/views/style.css").toExternalForm());
            helpStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("bfst/views/bongIcon.png")));
            helpStage.setScene(scene);
            helpStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openAbout() {
        try {
            Stage aboutStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/about.fxml"));
            Parent root = fxmlLoader.load();
            aboutStage.setTitle("About");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("bfst/views/style.css").toExternalForm());
            aboutStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("bfst/views/bongIcon.png")));
            aboutStage.setScene(scene);
            aboutStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openDevTools() {
        try {
            Stage devStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/devview.fxml"));
            DevController devController = new DevController(devStage, canvas);
            fxmlLoader.setController(devController);
            Parent root = fxmlLoader.load();
            devStage.setTitle("dev tools");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("bfst/views/style.css").toExternalForm());
            devStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("bfst/views/bongIcon.png")));
            devStage.setScene(scene);
            devStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void placePin() {
        try {
            Point2D point2D = canvas.getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
            canvas.setPin((float) point2D.getX(), (float) point2D.getY());
            showPinMenu();
        } catch (NonInvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    private void setLinePathForDrawedSquare(MouseEvent e) {
        try {
            Point2D corner0 = canvas.getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
            Point2D corner1 = canvas.getTrans().inverseTransform(lastMouse.getX(), e.getY());
            Point2D corner2 = canvas.getTrans().inverseTransform(e.getX(), e.getY());
            Point2D corner3 = canvas.getTrans().inverseTransform(e.getX(), lastMouse.getY());

            float[] floats = {
                    (float) corner0.getX(), (float) corner0.getY(),
                    (float) corner1.getX(), (float) corner1.getY(),
                    (float) corner2.getX(), (float) corner2.getY(),
                    (float) corner3.getX(), (float) corner3.getY(),
                    (float) corner0.getX(), (float) corner0.getY(),
            };
            LinePath linePath = new LinePath(floats);
            canvas.setDraggedSquare(linePath);
        } catch (NonInvertibleTransformException ex) {
            ex.printStackTrace();
        }
    }

    private void disableShortFastChoice() {
        shortButton.setSelected(true);
        fastButton.setDisable(true);
        shortButton.setDisable(true);
    }

    private void updateShowPublicTransport(boolean showPublicTransport) {
        ArrayList<Type> typesToBeDrawn = new ArrayList<>();
        if (showPublicTransport) {
            typesToBeDrawn.addAll(Arrays.asList(Type.getTypes()));
        } else {
            for (Type type : Type.getTypes()) {
                if (type != Type.RAILWAY) {
                    typesToBeDrawn.add(type);
                }
            }
        }

        canvas.setTypesToBeDrawn(typesToBeDrawn);
    }

    private void addItemToMyPoints(PointOfInterest poi) {
        MenuItem item = new MenuItem(poi.getName());
        item.setOnAction(a -> {
            canvas.setPin(poi.getLon(), poi.getLat());
            canvas.zoomToPoint(1, poi.getLon(), poi.getLat());
            showPinMenu();
        });
        myPoints.getItems().add(item);
    }

    private void zoomToArea(Point2D end) {
        Point2D inversedStart = null;
        Point2D inversedEnd = null;
        try {
            inversedStart = canvas.getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
            inversedEnd = canvas.getTrans().inverseTransform(end.getX(), end.getY());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
        Point2D centerPoint = new Point2D((inversedEnd.getX() + inversedStart.getX()) / 2, (inversedEnd.getY() + inversedStart.getY()) / 2);

        double windowAspectRatio = canvas.getWidth() / canvas.getHeight();
        double markedAspectRatio = (end.getX() - lastMouse.getX()) / (end.getY() - lastMouse.getY());
        double factor;

        if (windowAspectRatio < markedAspectRatio) {
            factor = Math.abs((canvas.getWidth() / (end.getX() -  lastMouse.getX())) * canvas.getTrans().getMxx());
        } else {
            factor = Math.abs((canvas.getHeight() / (end.getY() -  lastMouse.getY()) * canvas.getTrans().getMxx()));
        }
        if (factor > 2.2) {
            factor = 2.2;
        }
        canvas.zoomToPoint(factor, (float) centerPoint.getX(), (float) centerPoint.getY());


    }

    public void setPOIButton() {
        AtomicBoolean POIExists = new AtomicBoolean(false);

        if (poiController.POIContains(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY())) {
            POIExists.set(true);

            POIButton.setTooltip(setupTooltip("Remove point of interest"));
            POIButton.getStyleClass().removeAll("POIButton-add");
            POIButton.getStyleClass().add("POIButton-remove");
        } else {
            POIExists.set(false);
            POIButton.setTooltip(setupTooltip("Add to points of interest"));
            POIButton.getStyleClass().removeAll("POIButton-remove");
            POIButton.getStyleClass().add("POIButton-add");
        }

        POIButton.setOnAction(e -> {
            if (!POIExists.get()) {
                poiController.showAddPointDialog(currentPoint);
                myPoints.getItems().clear();
                poiController.loadPointsOfInterest();
                for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                    addItemToMyPoints(poi);
                }

                poiController.savePointsOfInterest();
                POIExists.set(true);
                setPOIButton();
            } else {
                poiController.removePOI(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
                myPoints.getItems().clear();
                for (PointOfInterest poi : PointsOfInterestController.getPointsOfInterest()) {
                    addItemToMyPoints(poi);
                }
                POIExists.set(false);
                setPOIButton();
            }
        });
    }

    public void showPinMenu() {
        setPOIButton();
        Node unprojected = MercatorProjector.unproject(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
        pointCoords.setText(-unprojected.getLat() + "°N " + unprojected.getLon() + "°E");

        if (model != null && model.getAddressKDTree() != null && canvas.getCurrentPin() != null) {
            currentAddress = (Address) model.getAddressKDTree().nearestNeighbor(new Point2D(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY()));
        }
            currentPoint = new Point2D(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());

        if (currentAddress == null) {
            pointAddress.setText("No nearby address");
        } else {
            double distance = distanceInMeters(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY(), currentAddress.getLon(), currentAddress.getLat());
            if (distance > 80) {
                pointAddress.setText("No nearby address");
            } else {
                pointAddress.setText(currentAddress.toString());
            }
        }


        pinInfo.setTranslateY(10);
        pinInfo.setVisible(true);
    }

    private double dist(Point2D p1, Point2D p2){
        var dx = p2.getX() - p1.getX();
        var dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy) * 0.56;
    }

    public void showDirectionsMenu() {
        noRouteFound.setVisible(false);
        noRouteFound.setManaged(false);

        setStartOrDestinationLabel(startAddress, startPoint, startLabel);

        setStartOrDestinationLabel(destinationAddress, destinationPoint, destinationLabel);

        if (startAddress == null || destinationAddress == null) {
            findRoute.setDisable(true);
        } else {
            findRoute.setDisable(false);
        }

        ArrayList<Instruction> instructions;
        if ((instructions = canvas.getRouteController().getInstructions()) != null) {
            directions.getChildren().clear();
            for (Instruction instruction : instructions) {
                Button button = new Button(instruction.getInstruction());
                button.getStyleClass().add("instruction");
                button.setOnAction(e -> {
                    canvas.zoomToNode(instruction.getNode());
                });
                directions.getChildren().add(button);
            }
            routeDistance.setText(canvas.getRouteController().distanceString());
            routeTime.setText(canvas.getRouteController().timeString());
        }

        if (canvas.getRouteController().getRoute() != null) {
            routeInfo.setVisible(true);
            routeInfo.setManaged(true);
        } else {
            routeInfo.setVisible(false);
            routeInfo.setManaged(false);
        }

        directionsInfo.setVisible(true);
    }

    private void setStartOrDestinationLabel(Address address, Point2D point, Label label) {
        if (address != null) {
            if (dist(point, address.getCentroid()) > 80) { // TODO should this use distanceInMeters() ?
                Node unprojected = MercatorProjector.unproject(address.getCentroid().getX(), address.getCentroid().getY());
                label.setText(-unprojected.getLat() + "°N " + unprojected.getLon() + "°E");
            } else {
                label.setText(address.toString());
            }
        } else {
            label.setText("Not set");
        }
    }

    private double distanceInMeters(float pinX, float pinY, float addressX, float addressY) {
        double meterMultiplier = - (MercatorProjector.unproject(pinX, pinY).getLat()) / 100;
        double distance = Math.sqrt(Math.pow(pinX - addressX, 2) + Math.pow(pinY - addressY, 2));
        return distance * meterMultiplier;
    }

    public void hidePinInfo(){
        pinInfo.setVisible(false);
    }

    public void saveFileOnClick(ActionEvent e){
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Binary file (*.bin)", "*.bin");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("myMap");
            File file = fileChooser.showSaveDialog(stage);
            if(file != null){
                FileController.saveBinary(file, model);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Saved successfully");
                alert.showAndWait();
            }
        } catch (Exception exception) {
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("Something unexpected happened, please try again");
            alert.showAndWait();
            exception.printStackTrace();
        }
    }

    public boolean loadFileOnClick(){
        try {
            List<String> acceptedFileTypes = new ArrayList<>();
            acceptedFileTypes.add("*.bin");
            acceptedFileTypes.add("*.osm");
            acceptedFileTypes.add("*.zip");

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Binary, OSM or ZIP file", acceptedFileTypes);
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("myMap");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                loadFile(file);
                return true;
            } 
            return false;
        } catch(FileTypeNotSupportedException exception) {
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("File type not supported: " + exception.getFileType());
            alert.showAndWait();
            return false;
        } catch (NullPointerException exception){
            exception.printStackTrace();
            return false;
        } catch (Exception exception) {
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("Something unexpected happened, please try again");
            alert.showAndWait();
            exception.printStackTrace();
            return false;
        }
    }

    public void loadFile(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        switch (fileExtension) {
            case ".bin":
                setModelFromBinary(is);
                break;
            case ".osm":
                canvas.setTypesToBeDrawn(new ArrayList<>());

                OSMReader reader = new OSMReader(is);
                this.model = new Model(reader);
                reader.destroy();
                reader = null;
                mapCanvasWrapper.mapCanvas.setModel(model);

                ArrayList<Type> list = new ArrayList<>(Arrays.asList(Type.getTypes()));
                canvas.setTypesToBeDrawn(list);
                break;
            case ".zip":
                loadFile(FileController.loadZip(file));
                break;
            default:
                is.close();
                throw new FileTypeNotSupportedException(fileExtension);
        }
        is.close();
    }

    private void setModelFromBinary(InputStream is) throws IOException, ClassNotFoundException {
        this.model = (Model) FileController.loadBinary(is);
        mapCanvasWrapper.mapCanvas.setModel(model);
    }

    private void swapStartAndDestination() {
        Address tempAddress = startAddress;
        Point2D tempPoint = startPoint;
        startAddress = destinationAddress;
        startPoint = destinationPoint;
        destinationAddress = tempAddress;
        destinationPoint = tempPoint;
        canvas.setRouteOrigin(startPoint);
        canvas.setRouteDestination(destinationPoint);
        showDirectionsMenu();
        canvas.repaint();
    }

    public MapCanvas getCanvas(){
        return canvas;
    }

}
