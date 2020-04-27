package bfst.controllers;

import bfst.App;
import bfst.OSMReader.MercatorProjector;
import bfst.OSMReader.Model;
import bfst.OSMReader.Node;
import bfst.OSMReader.OSMReader;
import bfst.addressparser.Address;
import bfst.addressparser.InvalidAddressException;
import bfst.canvas.*;
import bfst.exceptions.FileTypeNotSupportedException;
import bfst.routeFinding.Edge;
import bfst.routeFinding.Instruction;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainController {
    private Stage stage;
    private Model model;
    private Point2D lastMouse;
    private ArrayList<Address> tempBest = new ArrayList<>();
    private boolean hasBeenDragged = false;
    private Address destinationAddress;
    private Address startAddress;
    private Address currentAddress;

    private ToggleGroup vehicleGroup = new ToggleGroup();
    private RadioButton carButton = new RadioButton("Car");
    private RadioButton bikeButton = new RadioButton("Bicycle");
    private RadioButton walkButton = new RadioButton("Walk");

    private ToggleGroup shortFastGroup = new ToggleGroup();
    private RadioButton shortButton = new RadioButton("Shortest");
    private RadioButton fastButton = new RadioButton("Fastest");

    public MainController(Stage primaryStage){
        this.stage = primaryStage;
    }

    public void setDefaultMap(){
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bfst/copenhagen.bin");
            setModelFromBinary(is);
        }catch (Exception e){
            System.out.println("Failed to set default map");
        }
    }

    @FXML
    private StackPane stackPane;
    @FXML
    private MapCanvasWrapper mapCanvasWrapper;
    private MapCanvas canvas;
    @FXML
    private MenuItem loadClick;
    @FXML
    private MenuItem loadDefaultMap;
    @FXML
    private MenuItem saveAs;
    @FXML
    private MenuItem devtools;
    @FXML
    private MenuItem about;
    @FXML
    private MenuItem help;
    @FXML
    private TextField searchField;
    @FXML
    private VBox suggestions;

    @FXML
    private Menu myPoints;
    @FXML
    private HBox pinInfo;
    @FXML
    private Label pointAddress;
    @FXML
    private Label pointCoords;
    @FXML
    private Button POIButton;
    @FXML
    private Button setAsDestination;
    @FXML
    private Button setAsStart;
    @FXML
    private VBox routeInfo;
    @FXML
    private Label routeDistance;
    @FXML
    private Label routeTime;
    @FXML
    private VBox directions;
    @FXML
    private Menu view;
    @FXML
    private CheckMenuItem publicTransport;
    @FXML
    private CheckMenuItem darkMode;
    @FXML
    private CheckMenuItem hoverToShowStreet;
    @FXML
    private MenuItem zoomToArea;
    @FXML
    private Button findRoute;
    @FXML
    private VBox directionsInfo;
    @FXML
    private Label startLabel;
    @FXML
    private Label destinationLabel;
    @FXML
    private HBox vehicleSelection;
    @FXML
    private HBox shortestFastestSelection;
    @FXML
    private Label noRouteFound;
    @FXML
    private Button cancelRoute;

    private boolean shouldPan = true;
    private boolean showStreetOnHover = false;
    private String tempQuery = "";

    @FXML
    public void initialize() {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
            setDefaultMap();
            loadPointsOfInterest();

            for (PointOfInterest poi : canvas.getPointsOfInterest()) {
                addItemToMyPoints(poi);
            }
        });

        loadClick.setOnAction(this::loadFileOnClick);

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
                placeOrRemovePin();
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

        searchField.focusedProperty().addListener((obs,oldVal,newVal) -> {
            if (newVal) {
                searchField.setText(tempQuery);
                searchField.positionCaret(searchField.getText().length());
                KeyEvent press = new KeyEvent(searchField,searchField,KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
                searchField.fireEvent(press);
                searchField.positionCaret(searchField.getText().length());
            }
        });

        searchField.textProperty().addListener((obs,oldVal,newVal) -> {
            hideAddPOIButton();
            if (searchField.isFocused()) setTempQuery(searchField.getText());
            if(searchField.getText().length() == 0) suggestions.getChildren().clear();
            canvas.nullPin();
        });

        searchField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
            }
            if (event.getCode() == KeyCode.DOWN) {
                if(suggestions.getChildren().size() > 0) {
                    suggestions.getChildren().get(0).requestFocus();
                }
                event.consume();
            }
        });

        searchField.setOnAction(e -> {
            if(suggestions.getChildren().size() > 0) {
                Address a = (Address) suggestions.getChildren().get(0).getUserData();
                goToAddress(a);
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
            canvas.repaint(26);
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

        setAsDestination.setTooltip(new Tooltip("Set as destination"));
        setAsDestination.setOnAction(e -> {
            canvas.clearRoute();
            destinationAddress = currentAddress;
            canvas.setRouteDestination(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
            showDirectionsMenu();
        });

        setAsStart.setTooltip(new Tooltip("Set as start"));
        setAsStart.setOnAction(e -> {
            canvas.clearRoute();
            startAddress = currentAddress;
            canvas.setRouteOrigin(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
            showDirectionsMenu();
        });

        findRoute.setOnAction(e -> {
            findRouteFromGivenInputs();
            showDirectionsMenu();
        });

        canvas.setOnMouseMoved(e -> {
            if (showStreetOnHover) {
                showStreetNearMouse(e);
            }

        });

        setRouteOptionButtons();

    }

    private void goToAddress(Address a) {
        setTempQuery(a.toString());
        searchField.setText(a.toString());
        searchField.positionCaret(searchField.getText().length());
        canvas.zoomToPoint(1, a.getLon(),  a.getLat());
        canvas.setPin(a.getLon(), a.getLat());
        suggestions.getChildren().clear();
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
        bikeButton.setToggleGroup(vehicleGroup);
        walkButton.setToggleGroup(vehicleGroup);
        carButton.setSelected(true);
        vehicleSelection.getChildren().addAll(carButton, bikeButton, walkButton);

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
        shortestFastestSelection.getChildren().addAll(shortButton, fastButton);

        cancelRoute.setOnAction(e -> {
            canvas.clearRoute();
            startAddress = null;
            destinationAddress = null;
            canvas.clearOriginDestination();
            directionsInfo.setVisible(false);
        });
    }

    private void showStreetNearMouse(MouseEvent e) {
        try {
            Point2D translatedCoords = canvas.getTrans().inverseTransform(e.getX(), e.getY());
            Node nearestNode = (Node) model.getRoadKDTree().nearestNeighbor(translatedCoords, "Car");
            long nodeAsLong = nearestNode.getAsLong();
            Edge streetEdge = model.getGraph().getAdj().get(nodeAsLong).get(0);
            double bestAngle = Double.POSITIVE_INFINITY;


            Point2D mouseRelativeToNodeVector = new Point2D(translatedCoords.getX() - nearestNode.getLon(), translatedCoords.getY() - nearestNode.getLat());

            for (Edge edge : model.getGraph().getAdj().get(nearestNode.getAsLong())) {
                Node otherNode = edge.otherNode(nodeAsLong);
                Point2D otherNodeRelativeToNodeVector = new Point2D(otherNode.getLon() - nearestNode.getLon(), otherNode.getLat() - nearestNode.getLat());

                double angle = Math.acos((mouseRelativeToNodeVector.getX() * otherNodeRelativeToNodeVector.getX() + mouseRelativeToNodeVector.getY() * otherNodeRelativeToNodeVector.getY()) / (mouseRelativeToNodeVector.magnitude() * otherNodeRelativeToNodeVector.magnitude()));

                if (angle < bestAngle) {
                    bestAngle = angle;
                    streetEdge = edge;
                }
            }

            String streetName = streetEdge.getStreet().getName();
            if (streetName == null) {
                streetName = "Unnamed street";
            }
            canvas.repaint(25);
            canvas.drawEdge(streetEdge);

            if (canvas.getShowStreetNodeCloseToMouse()) {
                canvas.drawNode(nearestNode);
            }

            canvas.drawStreetName(translatedCoords, streetName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findRouteFromGivenInputs() {
        RadioButton selectedVehicleButton = (RadioButton) vehicleGroup.getSelectedToggle();
        String vehicle = selectedVehicleButton.getText();
        RadioButton selectedShortFastButton = (RadioButton) shortFastGroup.getSelectedToggle();
        boolean shortestRoute = selectedShortFastButton.getText().equals("Shortest");

        long startRoadId = ((Node) model.getRoadKDTree().nearestNeighbor(startAddress.getCentroid(), vehicle)).getAsLong();
        long destinationRoadId = ((Node) model.getRoadKDTree().nearestNeighbor(destinationAddress.getCentroid(), vehicle)).getAsLong(); //TODO refactor as method
        try {
            noRouteFound.setText("");
            canvas.setDijkstra(startRoadId, destinationRoadId, vehicle, shortestRoute);
        } catch (Exception ex) {
            noRouteFound.setText("No route found");
        }
    }

    private void openHelp() {
        try {
            Stage helpStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/help.fxml"));
            Parent root = fxmlLoader.load();
            helpStage.setTitle("Help");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("bfst/views/style.css").toExternalForm());
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
            devStage.setScene(scene);
            devStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void placeOrRemovePin() {
        try {
            if (canvas.getCurrentPin() == null) {
                Point2D point2D = canvas.getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
                canvas.setPin((float) point2D.getX(), (float) point2D.getY());
                showPinMenu();
            } else {
                canvas.nullPin();
                hidePinMenu();
            }

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

    private SVGPath createPath(String svgString) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("icon");
        path.setContent(svgString);
        return path;
    }

    public void setPOIButton() {
        AtomicBoolean POIExists = new AtomicBoolean(false);

        if (canvas.POIContains(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY())) {
            POIExists.set(true);
            POIButton.setTooltip(new Tooltip("Remove point of interest"));
            POIButton.getStyleClass().removeAll("POIButton-add");
            POIButton.getStyleClass().add("POIButton-remove");
        } else {
            POIExists.set(false);
            POIButton.setTooltip(new Tooltip("Add to points of interest"));
            POIButton.getStyleClass().removeAll("POIButton-remove");
            POIButton.getStyleClass().add("POIButton-add");
        }

        POIButton.setOnAction(e -> {
            if (!POIExists.get()) {
                addPointOfInterest();
                savePointsOfInterest();
                POIExists.set(true);
                setPOIButton();
            } else {
                canvas.removePOI(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
                savePointsOfInterest();
                myPoints.getItems().clear();
                for (PointOfInterest poi : canvas.getPointsOfInterest()) {
                    addItemToMyPoints(poi);
                }
                POIExists.set(false);
                setPOIButton();
            }
        });
    }

    private void query(String query) {
        ArrayList<Address> addresses = model.getAddresses();
        Address inputAdress = null;
        query = query.toLowerCase();
        try {
            inputAdress = Address.parse(query);
            int index = Collections.binarySearch(addresses, inputAdress);
            tempBest = new ArrayList<>();
            for (int i = 0; i < 5; i++){
                if(index < 0){
                    tempBest.add(addresses.get(-index-1+i));
                } else {
                    tempBest.add(addresses.get(index+i));
                }
            }
        } catch (InvalidAddressException e) {
            System.out.println("invalid address");
        }
    }

    public void setTempQuery(String newQuery){
        tempQuery = newQuery;
        query(tempQuery);
        reGenSuggestions();
    }

    public void reGenSuggestions(){
        ArrayList<Address> best = tempBest;
        ArrayList<javafx.scene.Node> bs = new ArrayList<>();
        for (Address address : best) {
            String addressString = address.toString();

                Button b = new Button();
                b.setUserData(address);

                b.setText(addressString);
                b.getStyleClass().add("suggestion");
                b.setOnAction(e -> {
                    goToAddress((Address) b.getUserData());
                });
                b.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB) {
                        setTempQuery(((Address) ((Button) event.getSource()).getUserData()).toString());
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
                        Address a = (Address) b.getUserData();
                        searchField.setText(a.toString());
                        peekAddress(a);
                    }
                    
                });
                bs.add(b);
        }
        updateSuggestions(bs);
    }

    public void updateSuggestions(ArrayList<javafx.scene.Node> bs){
        suggestions.getChildren().clear();
        for (javafx.scene.Node b : bs) suggestions.getChildren().add(b);
    }

    public void showPinMenu() {
        setPOIButton();
        Node unprojected = MercatorProjector.unproject(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
        pointCoords.setText(-unprojected.getLat() + "°N " + unprojected.getLon() + "°E");

        currentAddress = (Address) model.getAddressKDTree().nearestNeighbor(new Point2D(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY()));
        pointAddress.setText(currentAddress.toString());
        double distance = distance(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY(), currentAddress.getLon(), currentAddress.getLat());
        System.out.println("distance: " + distance);
        if (distance > 50) {
            pointAddress.setText("No nearby address");
        }

        pinInfo.setTranslateY(10);
        pinInfo.setVisible(true);
    }

    public void showDirectionsMenu() {
        if (startAddress != null) {
            startLabel.setText("Start: " + startAddress.toString());
        } else {
            startLabel.setText("Start: Not set");
        }

        if (destinationAddress != null) {
            destinationLabel.setText("Destination: " + destinationAddress.toString());
        } else {
            destinationLabel.setText("Destination: Not set");
        }

        if (startAddress == null || destinationAddress == null) {
            findRoute.setDisable(true);
        } else {
            findRoute.setDisable(false);
        }

        if (canvas.getDescription() != null) {
            directions.getChildren().clear();
            for (Instruction instruction : canvas.getDescription()) {
                Button button = new Button(instruction.getInstruction());
                button.getStyleClass().add("instruction");
                button.setOnAction(e -> {
                    canvas.zoomToNode(instruction.getNode());
                });
                directions.getChildren().add(button);
            }
            routeDistance.setText("Route length: " + canvas.distanceString());
            routeTime.setText("Expected time: " + canvas.timeString());
        }

        if (canvas.getRoute() != null) {
            routeInfo.setVisible(true);
            routeInfo.setManaged(true);
        } else {
            routeInfo.setVisible(false);
            routeInfo.setManaged(false);
        }

        directionsInfo.setVisible(true);
    }

    private double distance(float pinX, float pinY, float addressX, float addressY) {
        System.out.println("clicked: " + pinX + " " +pinY);
        System.out.println("address: " + addressX + " " + addressY);
        double meterMultiplier = - (MercatorProjector.unproject(pinX, pinY).getLat()) / 100;
        System.out.println("multiplier: " + meterMultiplier);
        double distance = Math.sqrt(Math.pow(pinX - addressX, 2) + Math.pow(pinY - addressY, 2));
        return distance * meterMultiplier;
    }

    public void hidePinMenu() {
        pinInfo.setVisible(false);
    }

    public void hideAddPOIButton(){
        pinInfo.setVisible(false);
    }

    public int[] matches(String query, String address){
        String regex = ".*?(?<match>" + query.toLowerCase() + ").*";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(address.toLowerCase());
        if (m.find() && m.group("match") != null && query.length() > 0) {
            return new int[]{m.start("match"), m.end("match")};
        } else {
            return null;
        }
    }


    public void saveFileOnClick(ActionEvent e){
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Binary file (*.bin)", "*.bin");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("myMap");
            File file = fileChooser.showSaveDialog(stage);
            if(file != null){
                saveBinary(file, model);
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

    public void loadFileOnClick(ActionEvent e){
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
            }
        } catch(FileTypeNotSupportedException exception) {
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("File type not supported: " + exception.getFileType());
            alert.showAndWait();
        } catch (NullPointerException exception){
            exception.printStackTrace();
        } catch (Exception exception) {
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("Something unexpected happened, please try again");
            alert.showAndWait();
            exception.printStackTrace();
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
                long time = -System.nanoTime();

                OSMReader reader = new OSMReader(is);
                this.model = new Model(reader);
                reader.destroy();
                reader = null;
                mapCanvasWrapper.mapCanvas.setModel(model);

                time += System.nanoTime();
                System.out.println("load osm: " + time/1000000f + "ms");

                ArrayList<Type> list = new ArrayList<>(Arrays.asList(Type.getTypes()));
                canvas.setTypesToBeDrawn(list);
                break;
            case ".zip":
                loadZip(file);
                break;
            default:
                is.close();
                throw new FileTypeNotSupportedException(fileExtension);
        }
        is.close();
    }

    private void setModelFromBinary(InputStream is) throws IOException, ClassNotFoundException {
        long time = -System.nanoTime();
        this.model = (Model) loadBinary(is);
        mapCanvasWrapper.mapCanvas.setModel(model);

        time += System.nanoTime();
        System.out.println("load binary: " + time/1000000f + "ms");
    }

    private Object loadBinary(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
        Object temp = ois.readObject();
        ois.close();
        return temp;
    }

    public void saveBinary(File file, Serializable toBeSaved) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        oos.writeObject(toBeSaved);
        oos.close();
    }

    private void loadZip(File file) throws Exception {
        String fileName = "";
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file.getAbsolutePath()));
        ZipEntry zipEntry = zis.getNextEntry();
        String destFolder = System.getProperty("user.home") + File.separator + "Documents";
        File destDir = new File(destFolder);

        while (zipEntry != null) {
            File newFile = new File(destDir, zipEntry.getName());
            fileName = newFile.getName();
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        loadFile(new File(destFolder + File.separator + fileName));

    }

    private void savePointsOfInterest() {
        String destFolder = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin";
        File file = new File(destFolder);
        try {
            saveBinary(file, canvas.getPointsOfInterest());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPointOfInterest() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Save point of interest");
        dialog.setHeaderText("Enter the name of the point");
        dialog.setContentText("Name:");
        Optional<String> givenName = dialog.showAndWait();

        if (givenName.isPresent()) {
            PointOfInterest poi = new PointOfInterest(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY(), givenName.get());
            canvas.addToPOI(poi);
            addItemToMyPoints(poi);
        }
        savePointsOfInterest();
    }

    private void loadPointsOfInterest() {
        ArrayList<PointOfInterest> list = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin");
            list = (ArrayList<PointOfInterest>) loadBinary(is);
        } catch (Exception ignored){

        }
        canvas.setPOI(list);
    }

}
