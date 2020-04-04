package bfst.canvas;

import bfst.OSMReader.Node;
import bfst.OSMReader.NodeContainer;
import bfst.OSMReader.Way;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;

public class LinePath implements Drawable, Serializable {
    private float[] coords;
    private Type type;

    public LinePath(Way way, Type type, NodeContainer nodeContainer) {
        long[] nodes = way.getNodes();
        int nodesSize = way.getSize();
        coords = new float[nodesSize * 2];
        for (int i = 0 ; i < nodesSize ; ++i) {
            Node nd = nodeContainer.get(nodes[i]);
            if(nd != null) {
                coords[i * 2] = nd.getLon();
                coords[i * 2 + 1] = nd.getLat();
            }
        }
        this.type = type;
    }

    public LinePath(float[] coords) {
        this.coords = coords;
    }
/*
    public LinePath(Way way) {
        ArrayList<Node> nodes = way.getNodes();
        int nodesSize = nodes.size();
        coords = new float[nodesSize * 2];
        for (int i = 0 ; i < nodesSize ; ++i) {
            coords[i*2] = nodes.get(i).getLon();
            coords[i*2+1] = nodes.get(i).getLat();
        }
    }
*/
    public LinePath(Node tail, Node head) {
        coords = new float[4];
        coords[0] = tail.getLon();
        coords[1] = tail.getLat();
        coords[2] = head.getLon();
        coords[3] = head.getLat();
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
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2 ; i < coords.length ; i += 2) {
            gc.lineTo(coords[i], coords[i+1]);
        }
    }

    public void smartTrace(GraphicsContext gc, double scale){
        float lastX = coords[0];
        float lastY = coords[1];
        gc.moveTo(lastX,lastY);
        for (int i = 2 ; i < coords.length - 2 ; i += 2) {
            float nextX = coords[i];
            float nextY = coords[i+1];
            float diffX = nextX - lastX;
            float diffY = nextY - lastY;
            double hypotenuse = Math.sqrt(Math.pow(diffX,2) + Math.pow(diffY,2));
            double distToNext = scale * hypotenuse;
            if(2 < distToNext){
                gc.lineTo(nextX,nextY);
                lastX = nextX;
                lastY = nextY;
            }
        }

        gc.lineTo(coords[coords.length-2],coords[coords.length-1]);
    }
}
