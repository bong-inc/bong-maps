package bfst.canvas;

import bfst.OSMReader.Bound;
import bfst.OSMReader.Model;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCanvas extends Canvas {
    private GraphicsContext gc;
    private Affine trans;
    private Model model;

    private List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

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
        gc.setFillRule(FillRule.EVEN_ODD);
        if(model != null) {
            for (Type type : typesToBeDrawn){
                if(type != Type.UNKNOWN) paintDrawablesOfType(type, pixelwidth);
            }
        }
    }

    public void setTypesToBeDrawn(List<Type> typesToBeDrawn){
        this.typesToBeDrawn = typesToBeDrawn;
        repaint();
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