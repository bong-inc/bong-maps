package bfst.controllers;

import bfst.App;
import bfst.OSMReader.Model;
import bfst.OSMReader.OSMReader;
import bfst.canvas.MapCanvas;
import bfst.canvas.MapCanvasWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MainController
 */
public class MainController {
    Stage stage;
    Model model;
    private Point2D lastMouse;

    public MainController(Stage primaryStage){
        this.stage = primaryStage;
    }

    public void setDefaultMap(){
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bfst/samsoe.bin");
            loadBinary(is);
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

    String tempQuery = "";

    @FXML
    public void initialize() {
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
            setDefaultMap();
        });

        loadClick.setOnAction(this::loadFileOnClick);

        canvas = mapCanvasWrapper.mapCanvas;

        canvas.setOnMousePressed(e -> {
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        canvas.setOnMouseDragged(e -> {
            canvas.pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
            lastMouse = new Point2D(e.getX(), e.getY());
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

    }

    public void setTempQuery(String newQuery){
        tempQuery = newQuery;
        reGenSuggestions();
    }

    public void reGenSuggestions(){
        String[] dummyAddresses = new String[]{"Jagtvej Copenhagen","Jagtvej Hillerød"};
        ArrayList<TextFlow> bs = new ArrayList<>();
        for (String address : dummyAddresses) {
            int[] matchRange = matches(searchField.getText(), address);
            if (matchRange != null) {
                TextFlow b = new TextFlow();
                b.setUserData(address);
                b.getChildren().add(new Text(address.substring(0,matchRange[0])));
                Text matched = new Text(address.substring(matchRange[0],matchRange[1]));
                matched.setStyle("-fx-font-weight: bold");
                b.getChildren().add(matched);
                b.getChildren().add(new Text(address.substring(matchRange[1])));
                b.getStyleClass().add("suggestion");
                b.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB) {
                        setTempQuery((String) ((TextFlow) event.getSource()).getUserData());
                        searchField.requestFocus();
                        event.consume();
                    }
                    if (event.getCode() == KeyCode.A && event.isControlDown()) {
                        searchField.requestFocus();
                    }
                });
                b.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) searchField.setText((String) b.getUserData());
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
                saveBinary(file);
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
        } catch(FileTypeNotSupportedException exception){
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("File type not supported: " +  exception.getFileType());
            alert.showAndWait();
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
                loadBinary(is);
                break;
            case ".osm":
                long time = -System.nanoTime();

                OSMReader reader = new OSMReader(is);
                this.model = new Model(reader);
                mapCanvasWrapper.mapCanvas.setModel(model);

                time += System.nanoTime();
                System.out.println("load osm: " + time/1000000f + "ms");
                break;
            default:
                throw new FileTypeNotSupportedException(fileExtension);
        }
    }

    private void loadBinary(InputStream is) throws IOException, ClassNotFoundException {
        long time = -System.nanoTime();

        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
        Object temp = ois.readObject();
        this.model = (Model) temp;
        ois.close();
        mapCanvasWrapper.mapCanvas.setModel(model);

        time += System.nanoTime();
        System.out.println("load binary: " + time/1000000f + "ms");
    }

    public void saveBinary(File file) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        oos.writeObject(model);
        oos.close();
    }
}