package bfst.canvas;

import bfst.OSMReader.Bound;
import bfst.OSMReader.Model;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
            Type[] typeArray = Type.getTypes();
            int arrayLength = typeArray.length;
            for (int i = 1; i < arrayLength; i++){
                paintDrawablesOfType(typeArray[i],pixelwidth);
            }
        }
    }

    public void resetView() {
        trans.setToIdentity();
        Bound b = model.getBound();
        pan(-(b.getMaxLon() + b.getMinLon())/2, -(b.getMaxLat() + b.getMinLat())/2);
        pan(getWidth()/2,getHeight()/2);

        float boundHeight = b.getMaxLat() - b.getMinLat();
        float boundWidth = b.getMaxLon() - b.getMinLon();
        float bound;
        float canvasScale;
        if(boundHeight > boundWidth){
            bound = boundHeight;
            canvasScale = (float) getHeight();
        } else {
            bound = boundWidth;
            canvasScale = (float) getWidth();
        }
        float factor = canvasScale/bound;
        zoom(factor,getWidth()/2,getHeight()/2);
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
        if (drawables != null) {
            gc.setLineWidth(type.getWidth() * pixelwidth);
            if (type.getFill()) gc.setFill(type.getColor());
            gc.setStroke(type.getColor());
            for (Drawable drawable : drawables) {
                drawable.draw(gc);
                if (type.getFill()) gc.fill();
            }
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