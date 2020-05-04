package bong.canvas;

import bong.OSMReader.NodeContainer;
import bong.OSMReader.Relation;
import bong.OSMReader.Way;
import javafx.geometry.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class PolyLinePath extends CanvasElement implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<LinePath> linePaths;
    private Range boundingBox;

    public PolyLinePath(Relation relation, NodeContainer nodeContainer) {
        linePaths = new ArrayList<>();
        ArrayList<Way> ways = relation.getWays();
        for (var way : ways) {
            linePaths.add(new LinePath(way, nodeContainer));
        }
        setBoundingBox();
    }

    @Override
    public void draw(Drawer gc, double scale, boolean smartTrace) {
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
        return boundingBox.getCentroid();
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
            if(l.getBoundingBox().getMinX() < minX) minX = l.getBoundingBox().getMinX();
            if(l.getBoundingBox().getMaxX() > maxX) maxX = l.getBoundingBox().getMaxX();
            if(l.getBoundingBox().getMinY() < minY) minY = l.getBoundingBox().getMinY();
            if(l.getBoundingBox().getMaxY() > maxY) maxY = l.getBoundingBox().getMaxY();
        }
        return new Range(minX, minY, maxX, maxY);
    }

    public void setBoundingBox() {
        boundingBox = mergeBoundingBoxes();
    }
}