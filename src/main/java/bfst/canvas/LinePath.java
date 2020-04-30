package bfst.canvas;

import bfst.canvas.CanvasElement;
import bfst.OSMReader.Node;
import bfst.OSMReader.NodeContainer;
import bfst.OSMReader.Way;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;

public class LinePath extends CanvasElement implements Drawable, Serializable {
    private float[] coords_;
    private Range boundingBox;
    private Type type;

    public LinePath(Way way, Type type, NodeContainer nodeContainer) {
        this(getCoordsFromNodeContainer(way, nodeContainer));
        this.type = type;
    }

    private static float[] getCoordsFromNodeContainer(Way way, NodeContainer nodeContainer) {
        long[] nodes = way.getNodes();
        int nodesSize = way.getSize();
        float[] coords_ = new float[nodesSize * 2];
        for (int i = 0 ; i < nodesSize ; ++i) {
            int index = nodeContainer.getIndex(nodes[i]);
            coords_[i * 2] = nodeContainer.getLonFromIndex(index);
            coords_[i * 2 + 1] = nodeContainer.getLatFromIndex(index);
        }
        return coords_;
    }

    public LinePath(float[] coords) {
        this.coords_ = coords;
        setBoundingBox();
    }

    public LinePath(Node tail, Node head) {
        this(new float[]{
            tail.getLon(),
            tail.getLat(),
            head.getLon(),
            head.getLat()
        });
    }

    public float[] getCoords(){
        return coords;
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        gc.beginPath();
        traceMethod(gc, scale, smartTrace);
        gc.stroke();
    }

    public void traceMethod(GraphicsContext gc, double scale, boolean smartTrace) {
        if (smartTrace) {
            smartTrace(gc, scale);
        } else {
            trace(gc);
        }
    }

    public void trace(GraphicsContext gc) {
        gc.moveTo(coords_[0], coords_[1]);
        for (int i = 2 ; i < coords_.length ; i += 2) {
            gc.lineTo(coords_[i], coords_[i+1]);
        }
    }

    public void smartTrace(GraphicsContext gc, double scale){
        float lastX = coords_[0];
        float lastY = coords_[1];
        gc.moveTo(lastX,lastY);
        for (int i = 2 ; i < coords_.length - 2 ; i += 2) {
            float nextX = coords_[i];
            float nextY = coords_[i+1];
            float diffX = nextX - lastX;
            float diffY = nextY - lastY;
            double hypotenuse = Math.sqrt(Math.pow(diffX,2) + Math.pow(diffY,2));
            double distToNext = scale * hypotenuse;
            if(3 < distToNext){
                gc.lineTo(nextX,nextY);
                lastX = nextX;
                lastY = nextY;
            }
        }

        gc.lineTo(coords_[coords_.length-2],coords_[coords_.length-1]);
    }

    @Override
    public Point2D getCentroid() {
        if(boundingBox == null) 
            return null;
        return getCenterFromRange(boundingBox);
    }

    @Override
    public Range getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void setBoundingBox() {
        float minX = Float.MAX_VALUE;
        float maxX = Float.NEGATIVE_INFINITY;
        for (int x = 0; x < coords_.length; x += 2) {
            if(coords_[x] < minX) minX = coords_[x];
            if(coords_[x] > maxX) maxX = coords_[x];
        }
        float minY = Float.MAX_VALUE;
        float maxY = Float.NEGATIVE_INFINITY;
        for (int y = 1; y < coords_.length; y += 2) {
            if(coords_[y] < minY) minY = coords_[y];
            if(coords_[y] > maxY) maxY = coords_[y];
        }
        this.boundingBox = new Range(minX, minY, maxX, maxY);
    }
}
