package bfst.controllers;

import bfst.App;
import bfst.OSMReader.KDTreeForEdges;
import bfst.OSMReader.MercatorProjector;
import bfst.OSMReader.Model;
import bfst.OSMReader.Node;
import bfst.OSMReader.OSMReader;
import bfst.addressparser.Address;
import bfst.addressparser.InvalidAddressException;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainController {
    Stage stage;
    Model model;
    private Point2D lastMouse;
    private ArrayList<Address> tempBest = new ArrayList<>();
    private boolean hasBeenDragged = false;
    private Address destinationAddress;
    private Address startAddress;
    private Address currentAddress;

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

    @FXML StackPane stackPane;
    @FXML MapCanvasWrapper mapCanvasWrapper;
    MapCanvas canvas;
    @FXML MenuItem loadClick;
    @FXML MenuItem loadDefaultMap;
    @FXML MenuItem saveAs;
    @FXML MenuItem devtools;
    @FXML MenuItem about;
    @FXML MenuItem help;
    @FXML TextField searchField;
    @FXML VBox suggestions;

    @FXML Menu myPoints;
    @FXML VBox pinInfo;
    @FXML Label pointAddress;
    @FXML Label pointCoords;
    @FXML Button POIButton;
    @FXML Button setAsDestination;
    @FXML Button setAsStart;
    @FXML VBox routeInfo;
    @FXML Label routeDistance;
    @FXML Label routeTime;
    @FXML VBox directions;
    @FXML Menu view;
    @FXML CheckMenuItem publicTransport;
    @FXML CheckMenuItem darkMode;
    @FXML MenuItem zoomToArea;
    @FXML Button findRoute;

    private boolean shouldPan = true;
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
        });

        canvas.setOnMouseReleased(e -> {
            if (!hasBeenDragged && shouldPan) {
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
        });

        searchField.focusedProperty().addListener((obs,oldVal,newVal) -> {
            if (newVal) {
                searchField.setText(tempQuery);
                searchField.positionCaret(searchField.getText().length());
                KeyEvent press = new KeyEvent(searchField,searchField,KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
                searchField.fireEvent(press);
            }
        });

        searchField.textProperty().addListener((obs,oldVal,newVal) -> {
            hideAddPOIButton();
            if (searchField.isFocused()) setTempQuery(searchField.getText());
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
                searchField.setText(a.toString());
                searchField.positionCaret(searchField.getText().length());
                canvas.zoomToPoint(1, a.getLon(),  a.getLat());
                canvas.setPin(a.getLon(), a.getLat());
                suggestions.getChildren().clear();
                showPinMenu();
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

        zoomToArea.setOnAction(e ->  {
            shouldPan = false;
        });

        about.setOnAction(e -> {
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
        });

        help.setOnAction(e -> {
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
        });

        setAsDestination.setOnAction(e -> {
            destinationAddress = currentAddress;
            canvas.setRouteDestination(destinationAddress.getLon(), destinationAddress.getLat());
        });

        setAsStart.setOnAction(e -> {
            startAddress = currentAddress;
            canvas.setRouteOrigin(startAddress.getLon(), startAddress.getLat());
        });

        findRoute.setOnAction(e -> {
            String vehicle = "Car";
            long startRoadId = ((Node) model.getRoadKDTree().nearestNeighbor(startAddress.getCentroid(), vehicle)).getAsLong();
            long destinationRoadId = ((Node) model.getRoadKDTree().nearestNeighbor(destinationAddress.getCentroid(), vehicle)).getAsLong(); //TODO refactor as method
            canvas.setDijkstra(startRoadId, destinationRoadId, vehicle, true);
        });
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
        if (canvas.POIContains(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY())) {
            POIButton.setText("Remove from my points of interest");
        } else {
            POIButton.setText("Add to my points of interest");
        }

        POIButton.setOnAction(e -> {
            if (POIButton.getText().equals("Add to my points of interest")) {
                addPointOfInterest();
                savePointsOfInterest();
                POIButton.setText("Remove from my points of interest");
            } else {
                canvas.removePOI(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
                savePointsOfInterest();
                myPoints.getItems().clear();
                for (PointOfInterest poi : canvas.getPointsOfInterest()) {
                    addItemToMyPoints(poi);
                }
                POIButton.setText("Add to my points of interest");
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
        ArrayList<TextFlow> bs = new ArrayList<>();
        for (Address address : best) {
            String addressString = address.toString();
            int[] matchRange = matches(searchField.getText(), addressString);
            if (matchRange != null) {
                TextFlow b = new TextFlow();
                b.setUserData(address);
                b.getChildren().add(new Text(addressString.substring(0,matchRange[0])));
                Text matched = new Text(addressString.substring(matchRange[0],matchRange[1]));
                matched.setStyle("-fx-font-weight: bold");
                b.getChildren().add(matched);
                b.getChildren().add(new Text(addressString.substring(matchRange[1])));
                b.getStyleClass().add("suggestion");
                b.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB) {
                        setTempQuery(((Address) ((TextFlow) event.getSource()).getUserData()).toString());
                        searchField.requestFocus();
                        searchField.positionCaret(searchField.getText().length());
                        event.consume();
                    }
                    if (event.getCode() == KeyCode.A && event.isControlDown()) {
                        searchField.requestFocus();
                    }
                });
                b.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) searchField.setText((String) b.getUserData().toString());
                });
                bs.add(b);
            }
        }
        updateSuggestions(bs);
    }

    public void showPinMenu() {
        setPOIButton();
        Node unprojected = MercatorProjector.unproject(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY());
        pointCoords.setText("Point at " + -unprojected.getLat() + "°N " + unprojected.getLon() + "°E");

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
            routeInfo.setVisible(true); //TODO vbox resizer ikke ordentligt
        } else {
            routeInfo.setVisible(false);
        }

        //TODO vis "ingen adresse fundet" hvis der er for langt til nærmeste adresse.
        currentAddress = (Address) model.getAddressKDTree().nearestNeighbor(new Point2D(canvas.getCurrentPin().getCenterX(), canvas.getCurrentPin().getCenterY()));
        pointAddress.setText(currentAddress.toString());

        pinInfo.setTranslateY(10);
        pinInfo.setVisible(true);
    }

    public void hidePinMenu() {
        pinInfo.setVisible(false);
    }

    public void hideAddPOIButton(){
        pinInfo.setVisible(false);
    }

    public void updateSuggestions(ArrayList<TextFlow> bs){
        suggestions.getChildren().clear();
        for (TextFlow b : bs) suggestions.getChildren().add(b);
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
