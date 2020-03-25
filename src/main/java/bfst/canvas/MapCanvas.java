package bfst.canvas;

import bfst.OSMReader.Bound;
import bfst.OSMReader.Model;

import bfst.OSMReader.Node;

import bfst.citiesAndStreets.*;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapCanvas extends Canvas {
    private GraphicsContext gc;
    private Affine trans;
    private Model model;

    private ScaleBar scaleBar;
    private boolean smartTrace = true;
    private boolean useRegularColors = true;

    private Pin currentPin;

    private boolean showCities = true;
    private boolean useDependentDraw = true;

    private boolean showStreets = true;


    private List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

    public MapCanvas() {
        this.gc = getGraphicsContext2D();
        this.trans = new Affine();
        this.scaleBar = new ScaleBar();
        repaint();
    }

    public void repaint() {
        long time = -System.nanoTime();

        gc.setTransform(new Affine());
        if (useRegularColors) {
            gc.setFill(Color.valueOf("#ade1ff"));
        } else {
            gc.setFill(Color.AQUA);
        }
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        double pixelwidth = 1 / Math.sqrt(Math.abs(trans.determinant()));
        gc.setFillRule(FillRule.EVEN_ODD);
        if (model != null) {
            for (Type type : typesToBeDrawn) {
                if (type != Type.UNKNOWN) {
                    if (useDependentDraw) {
                        if (type.getMinMxx() < trans.getMxx()) {
                            paintDrawablesOfType(type, pixelwidth, useRegularColors);
                        }
                    } else {
                        paintDrawablesOfType(type, pixelwidth, useRegularColors);
                    }
                }
            }

            if (showStreets) {
                for (Street street : model.getStreets()) {
                    StreetType type = street.getType();
                    if (useDependentDraw) {
                        if (trans.getMxx() > type.getMinMxx()) {
                            setValuesAndDrawStreet(pixelwidth, street, type);
                        }
                    } else {
                        setValuesAndDrawStreet(pixelwidth, street, type);
                    }
                }
            }
            gc.setStroke(Color.BLACK);
            model.getBound().draw(gc, pixelwidth, false);

            if (currentPin != null) currentPin.draw(gc, pixelwidth);

            if (showCities) {
                gc.setFill(Color.DARKGREY);
                for (City city : model.getCities()) {
                    CityType type = city.getType();
                    gc.setFont(new Font(pixelwidth * type.getFontSize()));
                    if (trans.getMxx() < type.getMaxMxx() && trans.getMxx() > type.getMinMxx()) {
                        city.draw(gc, pixelwidth, false);
                    }
                }
            }
            Dijkstra dijkstra = new Dijkstra(model.getGraph(), 4927474144L);

            gc.setStroke(Color.RED);
            for (Street street : dijkstra.pathTo(8468145L)) {
                street.draw(gc, pixelwidth, smartTrace);

            }
        }


        scaleBar.updateScaleBar(this);
        scaleBar.draw(gc, pixelwidth, false);

        time += System.nanoTime();
        System.out.println("repaint: " + time / 1000000f + "ms");
    }

    private void setValuesAndDrawStreet(double pixelwidth, Street street, StreetType type) {
        if (useRegularColors) {
            gc.setStroke(type.getColor());
        } else {
            gc.setStroke(type.getAlternateColor());
        }
        gc.setLineWidth(pixelwidth * type.getWidth());
        street.draw(gc, pixelwidth, smartTrace);
    }

    public void setTypesToBeDrawn(List<Type> typesToBeDrawn) {
        this.typesToBeDrawn = typesToBeDrawn;
        repaint();
    }

    public void setUseRegularColors(boolean shouldUseRegularColors) {
        useRegularColors = shouldUseRegularColors;
        repaint();
    }

    public void setTraceType(boolean shouldSmartTrace) {
        smartTrace = shouldSmartTrace;
        repaint();
    }

    public void setShowCities(boolean shouldShowCities) {
        showCities = shouldShowCities;
        repaint();
    }

    public void setUseDependentDraw(boolean shouldUseDependentDraw) {
        useDependentDraw = shouldUseDependentDraw;
        repaint();
    }

    public void setShowStreets(boolean showStreets) {
        this.showStreets = showStreets;
        repaint();
    }

    public void resetView() {
        trans.setToIdentity();
        Bound b = model.getBound();
        pan(-(b.getMaxLon() + b.getMinLon()) / 2, -(b.getMaxLat() + b.getMinLat()) / 2);
        pan(getWidth() / 2, getHeight() / 2);

        float boundHeight = b.getMaxLat() - b.getMinLat();
        float boundWidth = b.getMaxLon() - b.getMinLon();
        float bound;
        float canvasScale;
        if (boundHeight > boundWidth) {
            bound = boundHeight;
            canvasScale = (float) getHeight();
        } else {
            bound = boundWidth;
            canvasScale = (float) getWidth();
        }
        float factor = canvasScale / bound;
        zoom(factor, getWidth() / 2, getHeight() / 2);
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, double x, double y) {
        trans.prependScale(factor, factor, x, y);
        repaint();
    }

    private void paintDrawablesOfType(Type type, double pixelwidth, boolean useRegularColors) {
        ArrayList<Drawable> drawables = model.getDrawablesOfType(type);
        gc.setStroke(Color.TRANSPARENT);
        gc.setFill(Color.TRANSPARENT);
        if (drawables != null) {
            gc.setLineWidth(type.getWidth() * pixelwidth);
            if (useRegularColors) {
                if (type.shouldHaveFill()) gc.setFill(type.getColor());
                if (type.shouldHaveStroke()) gc.setStroke(type.getColor());
            } else {
                if (type.shouldHaveFill()) gc.setFill(type.getAlternateColor());
                if (type.shouldHaveStroke()) gc.setStroke(type.getAlternateColor());
            }
            for (Drawable drawable : drawables) {
                drawable.draw(gc, 1 / pixelwidth, smartTrace);
                if (type.shouldHaveFill()) gc.fill();
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;
        resetView();
    }


    public Point2D getModelCoordinates(double x, double y) {
        try {
            return trans.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Point2D getScreenCoordinates(double x, double y) {
        return trans.transform(x, y);
    }

    public void zoomToNode (Node node){
        trans.setToIdentity();
        pan(-node.getLon(), -node.getLat());
        zoom(1, 0, 0);
        pan(getWidth() / 2, getHeight() / 2);
        repaint();
    }

    public void setPin (Node node){
        currentPin = new Pin(node.getLon(), node.getLat(), 1);
        repaint();
    }

    public void nullPin () {
        currentPin = null;
        repaint();
    }
}
