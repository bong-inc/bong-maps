package bfst.citiesAndStreets;

import bfst.OSMReader.Node;
import bfst.OSMReader.Way;
import bfst.canvas.Drawable;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Street implements Drawable {
    private Boolean onewayCar;
    private Boolean bicycle; //residential, highway:cycleway, cycleway:track
    private Boolean walking; //foot, sidewalk, highway:footway
    private Boolean car;
    private Boolean onewayBicycle;
    private Integer maxspeed;
    private String highwayType;
    private Way way;
    private String name;

    public Street(ArrayList<Tag> tags, Way way) {
        for (Tag tag : tags) {
            switch (tag.getKey()) {
                case "highway":
                    highwayType = tag.getValue();
                    switch (tag.getValue()) {
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
                    maxspeed = Integer.parseInt(tag.getValue());
                    break;
                case "sidewalk":
                    walking = true;
                    break;
                case "cycleway":
                    bicycle = true;
                    break;
                case "name":
                    name = tag.getValue();
                    break;
                case "oneway":
                    onewayCar = true;
                    break;
                case "oneway:bicycle":
                    onewayBicycle = true;
                    break;
                case "foot":
                    if (tag.getValue().equals("yes") || tag.getValue().equals("designated")) {
                        walking = true;
                    }
                    break;
                case "bicycle":
                    if (tag.getValue().equals("yes") || tag.getValue().equals("designated")) {
                        bicycle = true;
                    }
                    break;
            }

        }

        if (maxspeed == null) {
            switch (highwayType) {
                case "motorway":
                    maxspeed = 130;
                    break;
                case "residential":
                case "service":
                    maxspeed = 50;
                    break;
                default:
                    maxspeed = 80;
                    break;
            }
        }

        this.way = way;
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        gc.beginPath();
        for (int i = 0; i < way.getNodes().size(); i++) {
            Node currentNode = way.getNodes().get(i);
            gc.lineTo(currentNode.getLon(), currentNode.getLat());
        }
        gc.stroke();
    }
}
