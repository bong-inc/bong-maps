package bfst.canvas;

import bfst.OSMReader.*;
import bfst.controllers.MainController;
import bfst.controllers.RouteController;
import bfst.routeFinding.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import java.util.*;

public class MapCanvas extends Canvas {
    private Drawer gc;
    private Affine trans;
    private Model model;

    private ScaleBar scaleBar;
    private boolean smartTrace = true;
    private boolean useRegularColors = true;
    private boolean renderFullScreen = true;
    private LinePath draggedSquare;
    private Node startNode;
    private Node destinationNode;
    private boolean showRoadNodes = false;

    private Pin currentPin;
    private RouteOriginIndicator currentRouteOrigin;
    private RouteDestinationIndicator currentRouteDestination;
    private RouteController routeController = new RouteController(this);

    private boolean showCities = true;
    private boolean useDependentDraw = true;
    private boolean showStreetNodeCloseToMouse = false;
    private boolean drawBound = false;

    private List<Type> typesToBeDrawn = Arrays.asList(Type.getTypes());
    
    public static boolean drawBoundingBox;

    private Range renderRange;

    public RouteController getRouteController() {
        return routeController;
    }

    public Affine getTrans() {
        return trans;
    }

    public RouteDestinationIndicator getCurrentRouteDestination() {
        return currentRouteDestination;
    }

    public RouteOriginIndicator getCurrentRouteOrigin() {
        return currentRouteOrigin;
    }

    public void setCurrentRouteDestination() {
        currentRouteDestination = new RouteDestinationIndicator(1,1,1);
    }

    public void setCurrentRouteOrigin() {
        currentRouteOrigin = new RouteOriginIndicator(1,1,1);
    }

    public Range getRenderRange() {
        return renderRange;
    }

    public ScaleBar getScaleBar() {
        return scaleBar;
    }

    public Model getModel() {
        return model;
    }

    public boolean isShowRoadNodes() {
        return showRoadNodes;
    }

    public void setShowRoadNodes(boolean showRoadNodes) {
        this.showRoadNodes = showRoadNodes;
    }

    public void clearOriginDestination() {
        currentRouteOrigin = null;
        currentRouteDestination = null;
        repaint();
    }

    public Pin getCurrentPin() {
        return currentPin;
    }

    public void setShowStreetNodeCloseToMouse(boolean newValue) {
        showStreetNodeCloseToMouse = newValue;
    }

    public void drawStreetName(Point2D location, String text) {
        gc.setFill(Color.BLACK);
        gc.fillText(text, location.getX() + 15, location.getY() - 15);
    }

    public boolean getShowStreetNodeCloseToMouse() {
        return showStreetNodeCloseToMouse;
    }

    public MapCanvas() {
        this.gc = new Drawer(getGraphicsContext2D());
        this.trans = new Affine();
        this.scaleBar = new ScaleBar();
    }

    public void repaint() {
        gc.setTransform(new Affine());
        if (useRegularColors) {
            gc.setFill(Type.WATER.getColor());
        } else {
            gc.setFill(Type.WATER.getAlternateColor());
        }
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        double pixelwidth = 1 / Math.sqrt(Math.abs(trans.determinant()));
        gc.setFillRule(FillRule.EVEN_ODD);

        updateSearchRange(pixelwidth);

        if (model != null) {
            for (Type type : typesToBeDrawn) {
                if (type != Type.UNKNOWN) {
                    if (useDependentDraw) {
                        if (type.getMinMxx() < trans.getMxx() && trans.getMxx() < type.getMaxMxx()) {
                            paintDrawablesOfType(type, pixelwidth, useRegularColors);
                        }
                    } else {
                        paintDrawablesOfType(type, pixelwidth, useRegularColors);
                    }
                }
            }

            if (routeController.getRoute() != null) {
                gc.setStroke(Color.valueOf("#69c7ff"));
                gc.setLineWidth(pixelwidth*3);
                LinePath drawableRoute;
                if ((drawableRoute = routeController.getDrawableRoute()) != null) {
                    drawableRoute.draw(gc, pixelwidth, smartTrace);
                }
                if(routeController.getInstructions() != null){
                    for(Instruction instruction : routeController.getInstructions()){
                        instruction.getIndicator().draw(gc,pixelwidth);
                    }
                }
            }

            if(drawBound) {
                gc.setStroke(Color.BLACK);
                model.getBound().draw(gc, pixelwidth, false);
            }

            if (currentRouteOrigin != null) currentRouteOrigin.draw(gc, pixelwidth);
            if (currentRouteDestination != null) currentRouteDestination.draw(gc, pixelwidth);
            if (currentPin != null) currentPin.draw(gc, pixelwidth);

            if (showCities) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(pixelwidth*2);
                
                if (useRegularColors) {
                    gc.setFill(Color.valueOf("#555555"));
                    gc.setStroke(Color.WHITE);
                } else {
                    gc.setFill(Color.WHITE);
                    gc.setStroke(Color.valueOf("#555555"));
                }

                gc.setTextAlign(TextAlignment.CENTER);
                for(CanvasElement element : model.getCitiesKdTree().rangeSearch(renderRange)){
                    element.draw(gc, pixelwidth, smartTrace);
                }
            }
        }

        scaleBar.updateScaleBar(this);
        gc.setStroke(useRegularColors ? Color.BLACK : Color.WHITE);
        scaleBar.draw(gc, pixelwidth, false);
        gc.setStroke(Color.BLACK);

        if(!renderFullScreen) renderRange.draw(gc, pixelwidth);

        if (useRegularColors) {
            gc.setStroke(Color.BLACK);
        } else {
            gc.setStroke(Color.WHITE);
        }
        if (draggedSquare != null) {
            draggedSquare.draw(gc, pixelwidth, false);
        }

        if (showRoadNodes) {
            drawNode(startNode);
            drawNode(destinationNode);
        }
    }

    public void setDraggedSquare(LinePath linePath) {
        draggedSquare = linePath;
        repaint();
    }

    public void updateSearchRange(double pixelwidth) {
        float w = (float) this.getWidth();
        float h = (float) this.getHeight();
        if(renderFullScreen){
            renderRange = new Range(
                    (float) ((-trans.getTx())* pixelwidth),
                    (float) ((-trans.getTy())* pixelwidth),
                    (float) ((-trans.getTx() + w)* pixelwidth),
                    (float) ((-trans.getTy() + h)* pixelwidth)
            );
        } else {
            renderRange = new Range(
                    (float) ((-trans.getTx() + w/2-100)* pixelwidth),
                    (float) ((-trans.getTy() + h/2-100)* pixelwidth),
                    (float) ((-trans.getTx() + w/2+100)* pixelwidth),
                    (float) ((-trans.getTy() + h/2+100)* pixelwidth)
            );
        }

    }

    public void setRenderFullScreen(boolean bool){
        renderFullScreen = bool;
    }

    public void showDijkstraTree() {
        Dijkstra dijkstra;
        if ((dijkstra = routeController.getDijkstra()) != null) {
            for (Map.Entry<Long, Edge> entry : dijkstra.getAllEdgeTo().entrySet()) {
                new LinePath(entry.getValue().getTailNode(), entry.getValue().getHeadNode()).draw(gc, 1, false);
            }
        }
    }

    public void drawEdge(Edge edge) {
        Paint prevStroke = gc.getStroke();
        gc.setStroke(Color.RED);
        new LinePath(edge.getTailNode(), edge.getHeadNode()).draw(gc, 1, false);
        gc.setStroke(prevStroke);
    }

    public void drawNode(Node node) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(20);
        new LinePath(node, node).draw(gc, 10, false);
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
        if (shouldZoom(factor)) {
            trans.prependScale(factor, factor, x, y);
            repaint();
        }
    }

    public boolean shouldZoom(double factor) {
        if (factor > 1) {
            if (trans.getMxx() < 2.2) {
                return true;
            } else {
                return false;
            }
        } else {
            if (trans.getMxx() > 0.0005) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void paintDrawablesOfType(Type type, double pixelwidth, boolean useRegularColors) {
        KDTree kdTree = model.getKDTreeByType(type);
        gc.setStroke(Color.TRANSPARENT);
        gc.setFill(Color.TRANSPARENT);
        if (kdTree != null) {
            setFillAndStroke(type, pixelwidth, useRegularColors);
            
            for(CanvasElement element : kdTree.rangeSearch(renderRange)){
                element.draw(gc, 1/pixelwidth, smartTrace);
                if (type.shouldHaveFill()) gc.fill();

                if(drawBoundingBox) {
                    element.getBoundingBox().draw(gc, pixelwidth/2f);
                }
            }
        }

    }

    private void setFillAndStroke(Type type, double pixelwidth, boolean useRegularColors) {
        gc.setLineWidth(type.getWidth() * pixelwidth);
        if (useRegularColors) {
            if (type.shouldHaveFill()) gc.setFill(type.getColor());
            if (type.shouldHaveStroke()) gc.setStroke(type.getColor());
        } else {
            if (type.shouldHaveFill()) gc.setFill(type.getAlternateColor());
            if (type.shouldHaveStroke()) gc.setStroke(type.getAlternateColor());
        }
    }

    public void setModel(Model model) {
        this.model = model;
        resetView();
    }

    public void setModelWithoutReset(Model model) {
        this.model = model;
    }


    public Point2D getModelCoordinates(double x, double y) {
        try {
            return trans.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void zoomToNode (Node node){
        zoomToPoint(1, node.getLon(), node.getLat());
    }

    public void zoomToPoint (double factor, float lon, float lat){
        trans.setToIdentity();
        pan(-lon, -lat);
        zoom(factor, 0, 0);
        pan(getWidth() / 2, getHeight() / 2);
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

    public void setRouteOrigin (Point2D point){
        if (point != null) {
            currentRouteOrigin = new RouteOriginIndicator((float) point.getX(), (float) point.getY(), 1);
        } else {
            currentRouteOrigin = null;
        }
        repaint();
    }

    public void setRouteDestination (Point2D point){
        if (point != null) {
            currentRouteDestination = new RouteDestinationIndicator((float) point.getX(), (float) point.getY(), 1);
        } else {
            currentRouteDestination = null;
        }
        repaint();
    }

    public boolean getRenderFullScreen(){
        return renderFullScreen;
    }

    public void setDrawBound(boolean drawBound){
        this.drawBound = drawBound;
    }

    public boolean getDrawBound(){
        return drawBound;
    }

    public void setStartDestPoint(Node start, Node dest) {
        this.startNode = start;
        this.destinationNode = dest;
    }

	public void showStreetNearMouse(MainController mainController, MouseEvent e) {
	    try {
	        Point2D translatedCoords = getTrans().inverseTransform(e.getX(), e.getY());
	        Node nearestNode = (Node) mainController.model.getRoadKDTree().nearestNeighborForEdges(translatedCoords, "Walk");
	        long nodeAsLong = nearestNode.getAsLong();
	        Edge streetEdge = mainController.model.getGraph().getAdj().get(nodeAsLong).get(0);
	        double bestAngle = Double.POSITIVE_INFINITY;
	
	
	        Point2D mouseRelativeToNodeVector = new Point2D(translatedCoords.getX() - nearestNode.getLon(), translatedCoords.getY() - nearestNode.getLat());
	
	        for (Edge edge : mainController.model.getGraph().getAdj().get(nearestNode.getAsLong())) {
	            Node otherNode = edge.otherNode(nodeAsLong);
	            Point2D otherNodeRelativeToNodeVector = new Point2D(otherNode.getLon() - nearestNode.getLon(), otherNode.getLat() - nearestNode.getLat());
	
	            double angle = Math.acos((mouseRelativeToNodeVector.getX() * otherNodeRelativeToNodeVector.getX() + mouseRelativeToNodeVector.getY() * otherNodeRelativeToNodeVector.getY()) / (mouseRelativeToNodeVector.magnitude() * otherNodeRelativeToNodeVector.magnitude()));
	
	            if (angle < bestAngle) {
	                bestAngle = angle;
	                streetEdge = edge;
	            }
	        }
	
	        String streetName = streetEdge.getStreet().getName();
	        if (streetName == null) {
	            streetName = "Unnamed street";
	        }
	        repaint();
	        drawEdge(streetEdge);
	
	        if (getShowStreetNodeCloseToMouse()) {
	            drawNode(nearestNode);
	        }
	
	        drawStreetName(translatedCoords, streetName);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
}
