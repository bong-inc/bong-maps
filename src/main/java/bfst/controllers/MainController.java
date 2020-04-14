package bfst.controllers;

import bfst.App;
import bfst.OSMReader.MercatorProjector;
import bfst.OSMReader.Model;
import bfst.OSMReader.OSMReader;
import bfst.addressparser.Address;
import bfst.addressparser.InvalidAddressException;
import bfst.canvas.MapCanvas;
import bfst.canvas.MapCanvasWrapper;
import bfst.canvas.PointOfInterest;
import bfst.canvas.Type;
import bfst.exceptions.FileTypeNotSupportedException;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    @FXML TextField searchField;
    @FXML VBox suggestions;
    @FXML MenuItem addPOI;
    @FXML Menu myPoints;

    String tempQuery = "";

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
            canvas.pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        canvas.setOnMouseReleased(e -> {
            if (!hasBeenDragged) {
                try {
                    Point2D point2D = canvas.getTrans().inverseTransform(lastMouse.getX(), lastMouse.getY());
                    canvas.setPin((float) point2D.getX(), (float) point2D.getY());

                } catch (NonInvertibleTransformException ex) {
                    ex.printStackTrace();
                }
            }
            hasBeenDragged = false;
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
            } catch (Exception ex){
                ex.printStackTrace();
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
            if (searchField.isFocused()) setTempQuery(searchField.getText());
            canvas.nullPin();
            System.out.println("CHANGED");
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
            System.out.println("action");
            if(suggestions.getChildren().size() > 0) {
                Address a = (Address) suggestions.getChildren().get(0).getUserData();
                searchField.setText(a.toString());
                searchField.positionCaret(searchField.getText().length());
                canvas.zoomToNode(a.node);
                canvas.setPin(a.node);
                suggestions.getChildren().clear();
            }
        });

        addPOI.setOnAction(e -> {
            addPointOfInterest();
        });
    }

    private void addItemToMyPoints(PointOfInterest poi) {
        MenuItem item = new MenuItem(poi.getName());
        item.setOnAction(a -> {
            canvas.setPin(poi.getLon(), poi.getLat());
        });
        myPoints.getItems().add(item);
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
            File file = new FileChooser().showSaveDialog(stage);
            if(file != null){
                saveBinary(file, model);
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
            File file = new FileChooser().showOpenDialog(stage);
            loadFile(file);
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
