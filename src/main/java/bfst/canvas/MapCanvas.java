package bfst.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class MapCanvas extends Canvas {
    private GraphicsContext gc;
    private Affine trans;

    public MapCanvas(){
        this.gc = getGraphicsContext2D();
        this.trans = new Affine();
        repaint();
    }

    public void repaint(){
        gc.setTransform(new Affine());
        gc.setFill(Color.valueOf("#ade1ff"));
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

}