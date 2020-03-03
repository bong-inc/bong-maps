package bfst.controllers;

import bfst.Debouncer;
import bfst.canvas.MapCanvas;
import bfst.canvas.MapCanvasWrapper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.concurrent.TimeUnit;

/**
 * MainController
 */
public class MainController {
    @FXML StackPane stackPane;
    @FXML MapCanvasWrapper mapCanvasWrapper;
    @FXML MapCanvas canvas;

    @FXML
    public void initialize() {

    }
}