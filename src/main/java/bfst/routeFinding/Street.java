package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.canvas.Drawable;
import bfst.canvas.LinePath;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;

public class Street implements Drawable, Serializable {
    private boolean onewayCar = false;
    private boolean bicycle = false; //residential, highway:cycleway, cycleway:track
    private boolean walking = false; //foot, sidewalk, highway:footway
    private boolean car = false;
    private boolean onewayBicycle = false;
    private int maxspeed = 0;
    private String name;
    private StreetType type;
    private double weight;


    private Node tailNode;
    private Node headNode;



    public Street(ArrayList<String> tags, Node tailNode, Node headnode, StreetType type) {
        this.tailNode = tailNode;
        this.headNode = headnode;
        this.type = type;
        weight = Math.sqrt(Math.pow(this.tailNode.getLon()  -this.headNode.getLon(), 2) + Math.pow(this.tailNode.getLat()-this.headNode.getLat(), 2));

        for (int i = 0; i < tags.size(); i += 2) {
            String value = tags.get(i + 1);
            switch (tags.get(i)) {

                case "highway":
                    switch (value) {
                        case "footway":
                        case "steps":
                            walking = true;
                            break;
                        case "cycleway":
                            bicycle = true;
                            break;
                        case "path":
                            walking = true;
                            bicycle = true;
                            break;
                        case "primary":
                        case "secondary":
                        case "tertiary":
                        case "motorway":
                        case "service":
                        case "motorway_link":
                            car = true;
                            break;
                        case "residential":
                            walking = true;
                            bicycle = true;
                            car = true;
                            break;
                        case "unclassified":
                            bicycle = true;
                            car = true;
                    }
                    break;
                case "maxspeed":
                    try {
                        maxspeed = Integer.parseInt(value);
                    } catch (Exception ignored) {

                    }
                    break;
                case "sidewalk":
                    walking = true;
                    break;
                case "cycleway":
                    bicycle = true;
                    break;
                case "name":
                    name = value;
                    break;
                case "oneway":
                    onewayCar = true;
                    break;
                case "oneway:bicycle":
                    onewayBicycle = true;
                    break;
                case "foot":
                    if (value.equals("yes") || value.equals("designated")) {
                        walking = true;
                    }
                    break;
                case "bicycle":
                    if (value.equals("yes") || value.equals("designated")) {
                        bicycle = true;
                    }
                    break;
            }

        }

        if (maxspeed == 0) {
            switch (type) {
                case MOTORWAY:
                    maxspeed = 130;
                    break;
                case OTHER:
                    maxspeed = 50;
                    break;
                default:
                    maxspeed = 80;
                    break;
            }
        }
        tags.clear();
    }

    public StreetType getType() {
        return type;
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

    public long other(long vertex) {
        if (vertex == tailNode.getAsLong()) {
            return headNode.getAsLong();
        } else {
            return tailNode.getAsLong();
        }
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        new LinePath(tailNode, headNode).draw(gc, scale, smartTrace);
    }
}
