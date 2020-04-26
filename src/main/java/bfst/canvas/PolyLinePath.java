package bfst.canvas;

import bfst.canvas.CanvasElement;
import bfst.OSMReader.NodeContainer;
import bfst.OSMReader.Relation;
import bfst.OSMReader.Way;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;

public class PolyLinePath extends CanvasElement implements Drawable, Serializable {
    private static final long serialVersionUID = -4838798038938840050L;
    ArrayList<LinePath> linePaths;
    Type type;
    private Range boundingBox;

    public PolyLinePath(Relation relation, Type type, NodeContainer nodeContainer) {
        linePaths = new ArrayList<>();
        ArrayList<Way> ways = relation.getWays();
        for (var way : ways) {
            linePaths.add(new LinePath(way, type, nodeContainer));
        }
        this.type = type;
        setBoundingBox();
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        gc.beginPath();
        for (LinePath linepath : linePaths) {
            linepath.traceMethod(gc, scale, smartTrace);
        }
        gc.stroke();
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

    private Range mergeBoundingBoxes() {
        if(linePaths.size() < 1) return null;
        float minX = Float.MAX_VALUE;
        float maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.MAX_VALUE;
        float maxY = Float.NEGATIVE_INFINITY;
        for(LinePath l : linePaths){
            if(l.getBoundingBox().minX < minX) minX = l.getBoundingBox().minX;
            if(l.getBoundingBox().maxX > maxX) maxX = l.getBoundingBox().maxX;
            if(l.getBoundingBox().minY < minY) minY = l.getBoundingBox().minY;
            if(l.getBoundingBox().maxY > maxY) maxY = l.getBoundingBox().maxY;
        }
        return new Range(minX, minY, maxX, maxY);
    }

    @Override
    public void setBoundingBox() {
        boundingBox = mergeBoundingBoxes();
    }
}