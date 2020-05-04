package bong.routeFinding;

import bong.OSMReader.Node;
import bong.canvas.CanvasElement;
import bong.canvas.Drawer;
import bong.canvas.Range;
import bong.util.Geometry;
import javafx.geometry.Point2D;
import java.io.Serializable;

public class Edge extends CanvasElement implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private float weight;
    private Street street;

    private long tailId;
    private float tailLon;
    private float tailLat;

    private long headId;
    private float headLon;
    private float headLat;

    public Edge(Node tailNode, Node headNode, Street street) {
        this.tailId = tailNode.getAsLong();
        this.tailLon = tailNode.getLon();
        this.tailLat = tailNode.getLat();
        this.headId = headNode.getAsLong();
        this.headLon = headNode.getLon();
        this.headLat = headNode.getLat();
        this.street = street;
        weight = (float) Math.sqrt(Math.pow(this.tailLon - this.headLon, 2)
                + Math.pow(this.tailLat - this.headLat, 2));
    }

    public Node getTailNode() {
        return new Node(tailId, tailLon, tailLat);
    }

    public Node getHeadNode() {
        return new Node(headId, headLon, headLat);
    }

    public double getWeight() {
        return weight;
    }

    public Street getStreet() {
        return street;
    }

    public long other(long vertex) {
        if (vertex == tailId) {
            return headId;
        } else {
            return tailId;
        }
    }

    public Node otherNode(long vertex) {
        if (vertex == tailId) {
            return new Node(headId, headLon, headLat);
        } else {
            return new Node(tailId, tailLon, tailLat);
        }
    }

    public Point2D getCentroid() {
        return new Point2D((tailLon + headLon) / 2, (tailLat + headLat) / 2);
    }

    public Range getBoundingBox() {
        return new Range(headLon < tailLon ? headLon : tailLon, // minX
                headLat < tailLat ? headLat : tailLat, // minY
                headLon > tailLon ? headLon : tailLon, // maxX
                headLat > tailLat ? headLat : tailLat // maxY
        );
    }

    public Node closestNode(Point2D query) {
        double distToTail = Geometry.distance(query.getX(), query.getY(), this.getTailNode().getLon(), this.getTailNode().getLat());
        double distToHead = Geometry.distance(query.getX(), query.getY(), this.getHeadNode().getLon(), this.getHeadNode().getLat());
        return distToTail < distToHead ? this.getTailNode() : this.getHeadNode();
    }

    @Override
    public void draw(Drawer gc, double scale, boolean smartTrace) {
        // ignored
    }
}
