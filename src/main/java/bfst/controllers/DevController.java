package bfst.controllers;

import bfst.canvas.MapCanvas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DevController {
    Stage stage;
    MapCanvas canvas;

    public DevController(Stage devStage, MapCanvas canvas){
        this.stage = devStage;
        this.canvas = canvas;
    }

    @FXML Button zoomIn;
    @FXML Button zoomOut;

    @FXML
    public void initialize(){

        zoomIn.setOnAction(e -> {
            canvas.zoom(2,canvas.getWidth()/2,canvas.getHeight()/2);
        });

        zoomOut.setOnAction(e -> {
            canvas.zoom(0.5,canvas.getWidth()/2,canvas.getHeight()/2);
        });

        

    }

}
