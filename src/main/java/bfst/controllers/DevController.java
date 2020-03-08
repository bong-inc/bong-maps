package bfst.controllers;

import bfst.canvas.MapCanvas;
import bfst.canvas.Type;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevController {
    Stage stage;
    MapCanvas canvas;
    List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

    public DevController(Stage devStage, MapCanvas canvas){
        this.stage = devStage;
        this.canvas = canvas;
    }

    @FXML Button zoomIn;
    @FXML Button zoomOut;
    @FXML FlowPane filterTypes;
    @FXML Button selectall;
    @FXML Button deselectall;

    @FXML
    public void initialize(){

        zoomIn.setOnAction(e -> {
            canvas.zoom(2,canvas.getWidth()/2,canvas.getHeight()/2);
        });

        zoomOut.setOnAction(e -> {
            canvas.zoom(0.5,canvas.getWidth()/2,canvas.getHeight()/2);
        });

        for (Type type : Type.getTypes()){
            CheckBox c = new CheckBox(type.name());
            c.setUserData(type);
            c.setSelected(true);
            c.setOnAction(e -> {
                updateTypesToBeDrawn();
            });
            filterTypes.getChildren().add(c);
        }

        selectall.setOnAction(e -> {
            for(Node node : filterTypes.getChildren()){
                CheckBox check = (CheckBox) node;
                check.setSelected(true);
            }
            canvas.setTypesToBeDrawn(Arrays.asList(Type.getTypes()));
        });

        deselectall.setOnAction(e -> {
            for(Node node : filterTypes.getChildren()){
                CheckBox check = (CheckBox) node;
                check.setSelected(false);
            }
            canvas.setTypesToBeDrawn(new ArrayList<>());
        });

    }

    private void updateTypesToBeDrawn() {
        typesToBeDrawn = new ArrayList<>();
        for(Node node : filterTypes.getChildren()){
            CheckBox check = (CheckBox) node;
            if(check.isSelected()) typesToBeDrawn.add((Type) check.getUserData());
        }
        canvas.setTypesToBeDrawn(typesToBeDrawn);
    }

}
