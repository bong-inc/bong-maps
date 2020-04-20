package bfst.canvas;

import bfst.OSMReader.CanvasElement;
import bfst.OSMReader.NodeContainer;
import bfst.OSMReader.Relation;
import bfst.OSMReader.Way;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class PolyLinePath extends CanvasElement implements Drawable {
    private static final long serialVersionUID = -4838798038938840050L;
    ArrayList<LinePath> linePaths;
    Type type;

    public PolyLinePath(Relation relation, Type type, NodeContainer nodeContainer) {
        linePaths = new ArrayList<>();
        ArrayList<Way> ways = relation.getWays();
        for (var way : ways) {
            linePaths.add(new LinePath(way, type, nodeContainer));
        }
        this.type = type;
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
        if(linePaths.size() > 0){
            return linePaths.get(0).getCentroid();
        } else {
            return new Point2D(0, 0); //TODO why is there a polyline path with 0 linepaths??
        }
    }
}