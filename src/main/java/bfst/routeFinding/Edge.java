package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.canvas.CanvasElement;
import bfst.canvas.Drawer;
import bfst.canvas.Range;
import javafx.geometry.Point2D;
import java.io.Serializable;

public class Edge extends CanvasElement implements Serializable {

    private float weight;
    private Street street;

    private Node tailNode;
    private Node headNode;

    public Edge(Node tailNode, Node headNode, Street street) {
        this.tailNode = tailNode;
        this.headNode = headNode;
        this.street = street;
        weight = (float) Math.sqrt(Math.pow(this.tailNode.getLon() - this.headNode.getLon(), 2)
                + Math.pow(this.tailNode.getLat() - this.headNode.getLat(), 2));
    }

    public Node getTailNode() {
        return tailNode;
    }

    public Node getHeadNode() {
        return headNode;
    }

    public double getWeight() {
        return weight;
    }

    public Street getStreet() {
        return street;
    }

    public long other(long vertex) {
        if (vertex == tailNode.getAsLong()) {
            return headNode.getAsLong();
        } else {
            return tailNode.getAsLong();
        }
    }

    public Node otherNode(long vertex) {
        if (vertex == tailNode.getAsLong()) {
            return headNode;
        } else {
            return tailNode;
        }
    }

    public Point2D getCentroid() {
        return new Point2D((tailNode.getLon() + headNode.getLon()) / 2, (tailNode.getLat() + headNode.getLat()) / 2);
    }

    public Range getBoundingBox() {
        float tailLon = tailNode.getLon();
        float tailLat = tailNode.getLat();
        float headLon = headNode.getLon();
        float headLat = headNode.getLat();
        return new Range(headLon < tailLon ? headLon : tailLon, // minX
                headLat < tailLat ? headLat : tailLat, // minY
                headLon > tailLon ? headLon : tailLon, // maxX
                headLat > tailLat ? headLat : tailLat // maxY
        );
    }

    @Override
    public void draw(Drawer gc, double scale, boolean smartTrace) {
        // ignored
    }
}
