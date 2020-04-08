package bfst.canvas;

import bfst.OSMReader.Bound;
import bfst.OSMReader.Model;

import bfst.OSMReader.Node;

import bfst.routeFinding.*;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class MapCanvas extends Canvas {
    private GraphicsContext gc;
    private Affine trans;
    private Model model;

    private ScaleBar scaleBar;
    private boolean smartTrace = true;
    private boolean useRegularColors = true;
    private ArrayList<Edge> route;
    private Dijkstra dijkstra;
    private LinePath drawableRoute;
    private double routeTime;
    private double routeDistance;

    ArrayList<String> description;

    private Pin currentPin;

    private boolean showCities = true;
    private boolean useDependentDraw = true;

    private List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());

    public Affine getTrans() {
        return trans;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

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

            if (route != null) {
                gc.setStroke(Color.RED);
                drawableRoute.draw(gc, pixelwidth, smartTrace);
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
        }

        scaleBar.updateScaleBar(this);
        scaleBar.draw(gc, pixelwidth, false);

        time += System.nanoTime();
        System.out.println("repaint: " + time / 1000000f + "ms");
    }

    public void setDijkstra(long startPoint, long endPoint, String vehicle, boolean shortestRoute) {
        long time = -System.nanoTime();
        dijkstra = new Dijkstra(model.getGraph(), startPoint, endPoint, vehicle, shortestRoute);
        time += System.nanoTime();
        System.out.println("Set dijkstra: " + time / 1000000f + "ms");

        setRoute();
        generateRouteInfo(route, vehicle);
    }

    public void showDijkstraTree() {
        if (dijkstra != null) {
            for (Map.Entry<Long, Edge> entry : dijkstra.getAllEdgeTo().entrySet()) {
                new LinePath(entry.getValue().getTailNode(), entry.getValue().getHeadNode()).draw(gc, 1, false);
            }
        }
    }

    public void setRoute() {
        route = dijkstra.pathTo(dijkstra.getLastNode(), 1);

        ArrayList<Edge> secondPart = dijkstra.pathTo(dijkstra.getLastNode(), 2);
        Collections.reverse(secondPart);
        route.addAll(secondPart);
        route = singleDirectRoute(route);

        float[] floats = new float[route.size() * 2 + 2];

        Edge firstEdge = route.get(0);

        floats[0] = firstEdge.getTailNode().getLon();
        floats[1] = firstEdge.getTailNode().getLat();

        for (int i = 2; i < route.size() * 2 + 2; i += 2) {
            Node currentNode = route.get((i - 2) / 2).getHeadNode();
            floats[i] = currentNode.getLon();
            floats[i + 1] = currentNode.getLat();
        }

        drawableRoute = new LinePath(floats);

        repaint();
    }

    public ArrayList<Edge> singleDirectRoute(ArrayList<Edge> route) {
        ArrayList<Edge> singleDirectedRoute = new ArrayList<>();

        Edge firstEdge = route.get(0);
        Node prevNode;

        if (firstEdge.getTailNode().getAsLong() == route.get(1).getHeadNode().getAsLong()) {
            prevNode = firstEdge.getHeadNode();
        } else {
            prevNode = firstEdge.getTailNode();
        }

        for (Edge edge : route) {
            Node otherNode = edge.otherNode(prevNode.getAsLong());
            Edge newEdge = new Edge(prevNode, otherNode, edge.getStreet());
            singleDirectedRoute.add(newEdge);
            prevNode = otherNode;
        }
        return  singleDirectedRoute;
    }

    //TODO har egentlig ikke noget med canvas at gøre, så skal nok flyttes
    public void generateRouteInfo(ArrayList<Edge> iterable, String vehicle) {

        description = new ArrayList<>();
        routeDistance = 0;
        routeTime = 0;

        Edge first = iterable.get(0);
        String prevEdgeName = first.getStreet().getName();
        double tempLength = 0;
        Edge prevEdge = first;
        int roundaboutCounter = 0;

        for (int i = 0; i < iterable.size(); i++) {
            Edge currEdge = iterable.get(i);

            if (currEdge.getStreet().getRole() == 2 && model.getGraph().getOutDegree(currEdge.getHeadNode().getAsLong(), vehicle) > 1) {
                roundaboutCounter++;
            }

            if (currEdge.getStreet().getName() != null) { //TODO veje uden navne ignoreres

                if (!prevEdgeName.equals(currEdge.getStreet().getName()) || (currEdge.getStreet().getRole() != 2 && prevEdge.getStreet().getRole() == 2)) {

                    Point2D prevVector = new Point2D(prevEdge.getHeadNode().getLon() - prevEdge.getTailNode().getLon(), prevEdge.getHeadNode().getLat() - prevEdge.getTailNode().getLat());
                    Point2D currVector = new Point2D(currEdge.getHeadNode().getLon() - currEdge.getTailNode().getLon(), currEdge.getHeadNode().getLat() - currEdge.getTailNode().getLat());
                    Point2D prevUnitVector = new Point2D(prevVector.getX() / prevVector.magnitude(), prevVector.getY() / prevVector.magnitude());
                    Point2D currUnitVector = new Point2D(currVector.getX() / currVector.magnitude(), currVector.getY() / currVector.magnitude());
                    Point2D resultingVector = new Point2D(currUnitVector.getX() - prevUnitVector.getX(), currUnitVector.getY() -  prevUnitVector.getY());
                    double turn = Math.atan2(resultingVector.getX(), resultingVector.getY()) * 180 / Math.PI;

                    addInstruction(prevEdgeName, tempLength);

                    if (currEdge.getStreet().getRole() == 3 && prevEdge.getStreet().getRole() == 1) {
                        description.add("Take the ramp onto the motorway");
                    } else if (currEdge.getStreet().getRole() != 1 && currEdge.getStreet().getRole() != 3 && prevEdge.getStreet().getRole() == 1) {
                        description.add("Take the off-ramp");
                    } else if (roundaboutCounter > 0) {
                        description.add("Take exit number " + roundaboutCounter + " in the roundabout");
                        roundaboutCounter = 0;
                    } else if (turn > 20) {
                        description.add("Angle: " + turn);
                        description.add("Turn right");
                    } else if (turn < -20) {
                        description.add("Angle: " + turn);
                        description.add("Turn left");
                    }

                    prevEdgeName = currEdge.getStreet().getName();
                    tempLength = currEdge.getWeight() * 0.56;
                } else {
                    tempLength += currEdge.getWeight() * 0.56;

                    if (i == iterable.size() - 1) {
                        addInstruction(prevEdgeName, tempLength);
                    }

                }
            } else {
                tempLength += currEdge.getWeight() * 0.56;
            }


            prevEdge = iterable.get(i);

            double distance = currEdge.getWeight() * 0.56;
            routeDistance += distance;

            switch (vehicle) {
                case "Car":
                    routeTime += distance / (currEdge.getStreet().getMaxspeed() / 3.6);
                    break;
                case "Walk":
                    routeTime += distance / 1.1; //estimate for walking speed, 1.1 m/s.
                    break;
                case "Bicycle":
                    routeTime += distance / 6; //6 m/s biking speed estimate.
                    break;
            }

        }
        description.add("You have arrived at your destination");

        BigDecimal bd = new BigDecimal(routeDistance);
        bd = bd.round(new MathContext(3));
        int roundedDistance = bd.intValue();
        double timeInMinutes = routeTime / 60;

        String distanceString;
        String timeString;
        int hourCount = 0;

        if (routeDistance >= 1000) {
            distanceString = (double) roundedDistance / 1000 + " km";
        } else {
            distanceString = roundedDistance + " m";
        }

        while (timeInMinutes >= 60) {
            hourCount++;
            timeInMinutes -= 60;
        }

        if (hourCount > 0) {
            timeString = hourCount + " h " + (int) timeInMinutes + " m";
        } else {
            timeString = (int) timeInMinutes + " m";
        }

        description.add("Total distance: " + distanceString);
        description.add("Estimated time: " + timeString);
    }

    private void addInstruction(String prevEdgeName, double tempLength) {
        BigDecimal bd = new BigDecimal(tempLength);
        bd = bd.round(new MathContext(2));
        int roundedLength = bd.intValue();
        if (roundedLength >= 10000) {
            description.add("Follow " + prevEdgeName + " for " + roundedLength / 1000 + " km");
        } else if (roundedLength > 1000) {
            description.add("Follow " + prevEdgeName + " for " + (double) roundedLength / 1000 + " km");
        } else {
            description.add("Follow " + prevEdgeName + " for " + roundedLength + " m");
        }
    }

    public void clearRoute() {
        route = null;
        dijkstra = null;
        drawableRoute = null;
        repaint();
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

    public Iterable<Edge> getRoute() {
        return route;
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

    public void setPin (float lon, float lat){
        currentPin = new Pin(lon, lat, 1);
        repaint();
    }

    public void nullPin () {
        currentPin = null;
        repaint();
    }
}
