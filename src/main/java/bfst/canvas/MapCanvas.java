package bfst.canvas;

import bfst.OSMReader.Bound;
import bfst.OSMReader.Model;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;

public class MapCanvas extends Canvas {
    private GraphicsContext gc;
    private Affine trans;
    private Model model;

    public MapCanvas(){
        this.gc = getGraphicsContext2D();
        this.trans = new Affine();
        repaint();
    }

    public void repaint(){
        gc.setTransform(new Affine());
        gc.setFill(Color.valueOf("#ade1ff"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        double pixelwidth = 1 / Math.sqrt(Math.abs(trans.determinant()));

        //gc.setFill(Paint.valueOf("#000"));
        //gc.fillRect(10.599514,-55.806709,1,1);

        if(model != null) {
            paintDrawablesOfType(Type.COASTLINE, pixelwidth);
            paintDrawablesOfType(Type.FARMFIELD, pixelwidth);
            paintDrawablesOfType(Type.FOREST, pixelwidth);
            paintDrawablesOfType(Type.BEACH, pixelwidth);
            paintDrawablesOfType(Type.BUILDING, pixelwidth);
            paintDrawablesOfType(Type.HIGHWAY, pixelwidth);
            paintDrawablesOfType(Type.WATER, pixelwidth);
            paintDrawablesOfType(Type.WATERWAY, pixelwidth);

        }
    }

    public void resetView() {
        trans.setToIdentity();
        Bound b = model.getBound();
        pan(-b.getMinLon(), -b.getMinLat());
        double boundHeight = b.getMaxLat() - b.getMinLat();
        double boundWidth = b.getMaxLon() - b.getMinLon();
        double bound;
        double canvasScale;
        if(boundHeight > boundWidth){
            bound = boundHeight;
            canvasScale = getHeight();
        } else {
            bound = boundWidth;
            canvasScale = getWidth();
        }
        double factor = canvasScale/bound;
        zoom(factor,0,0);
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, double x, double y) {
        trans.prependScale(factor, factor, x, y);
        repaint();
    }

    private void paintDrawablesOfType(Type type, double pixelwidth) {
        ArrayList<Drawable> drawables = model.getDrawablesOfType(type);
        gc.setLineWidth(type.getWidth() * pixelwidth);
        if(type.getFill()) gc.setFill(type.getColor());
        gc.setStroke(type.getColor());
        //gc.setStroke(Color.BLACK);
        for(Drawable drawable : drawables){
            drawable.draw(gc);
            if(type.getFill()) gc.fill();
        }
    }

    public Point2D toModelCoords(double x, double y) {
        try {
            return trans.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            // Troels siger at det her ikke kan ske
            e.printStackTrace();
            return null;
        }
    }

    public void setModel(Model model) {
        this.model = model;
        resetView();
    }
}