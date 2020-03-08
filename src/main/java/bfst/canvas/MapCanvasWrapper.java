package bfst.canvas;

import javafx.scene.layout.Pane;

public class MapCanvasWrapper extends Pane {

    public final MapCanvas mapCanvas;

    public MapCanvasWrapper(){
        mapCanvas = new MapCanvas();
        getChildren().add(mapCanvas);

        heightProperty().addListener((obs, oldVal, newVal) -> {
            mapCanvas.setHeight((double) newVal);
            mapCanvas.repaint();
        });

        widthProperty().addListener((obs, oldVal, newVal) -> {
            mapCanvas.setWidth((double) newVal);
            mapCanvas.repaint();
        });

        mapCanvas.repaint();
    }
}
