package bfst.OSMReader;

import bfst.canvas.CanvasElement;
import bfst.canvas.Range;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.function.LongSupplier;

public class Node extends CanvasElement implements LongSupplier, Serializable {
    private long id;
    private float lon;
    private float lat;
    private static final long serialVersionUID = 4076268921174823754L;

    public Node(long id, float lon, float lat){
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    @Override
    public long getAsLong() {
        return id;
    }

    @Override
    public String toString() {
        return "Node, lat:" + lat + " lon:" + lon + " ID:" + id;
    }

    @Override
    public Point2D getCentroid() {
        return new Point2D(this.lon, this.lat);
    }

    @Override
    public Range getBoundingBox() {
        return new Range(this.lon, this.lat, this.lon, this.lat);
    }

    @Override
    public void setBoundingBox() {
        //Ignored
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {

    }
}
