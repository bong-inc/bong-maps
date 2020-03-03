package bfst.controllers;

import bfst.OSMReader.Model;
import bfst.OSMReader.OSMReader;
import bfst.canvas.MapCanvas;
import bfst.canvas.MapCanvasWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

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
    @FXML StackPane stackPane;
    @FXML MapCanvasWrapper mapCanvasWrapper;
    MapCanvas canvas;
    @FXML MenuItem zoomIn;
    @FXML MenuItem zoomOut;
    @FXML MenuItem loadClick;

    @FXML
    public void initialize() {
        loadClick.setOnAction(this::loadFileOnClick);

        canvas = mapCanvasWrapper.mapCanvas;

        canvas.setOnMousePressed(e -> {
            Point2D mc = canvas.toModelCoords(e.getX(), e.getY());
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        canvas.setOnMouseDragged(e -> {
            canvas.pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        zoomIn.setOnAction(e -> {
            canvas.zoom(2,0,0);
        });

        zoomOut.setOnAction(e -> {
            canvas.zoom(0.5,0,0);
        });
    }

    public void loadFileOnClick(ActionEvent e){
        File file = new FileChooser().showOpenDialog(stage);
        try {
            loadFile(file);
        } catch(FileTypeNotSupportedException exception){
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("File type not supported: " +  exception.getFileType());
            alert.showAndWait();
        }
        catch (Exception exception) {
            Alert alert = new Alert((Alert.AlertType.ERROR));
            alert.setHeaderText("Something unexpected happened, please try again");
            alert.showAndWait();
            exception.printStackTrace();
        }
    }

    public void loadFile(File file) throws Exception {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        switch (fileExtension) {
            case ".bin":
                //TODO loadBinary(file);
                break;
            case ".osm":
                OSMReader reader = new OSMReader(new FileInputStream(file));
                this.model = new Model(reader);
                mapCanvasWrapper.mapCanvas.setModel(model);
                break;
            default:
                throw new FileTypeNotSupportedException(fileExtension);
        }
    }
}