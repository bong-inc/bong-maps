package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.canvas.Drawable;
import bfst.canvas.LinePath;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;

public class Edge implements Drawable, Serializable {

    private double weight;
    private Street street;

    private Node tailNode;
    private Node headNode;



    public Edge(Node tailNode, Node headNode, Street street) {
        this.tailNode = tailNode;
        this.headNode = headNode;
        this.street = street;
        weight = Math.sqrt(Math.pow(this.tailNode.getLon() - this.headNode.getLon(), 2) + Math.pow(this.tailNode.getLat() - this.headNode.getLat(), 2));
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

    //TODO optimer tegning
    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        new LinePath(tailNode, headNode).draw(gc, scale, smartTrace);
    }
}
