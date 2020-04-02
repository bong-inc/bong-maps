package bfst.routeFinding;

import java.io.Serializable;
import java.util.ArrayList;

public class Street implements Serializable {

    private boolean onewayCar = false;
    private boolean bicycle = false; //residential, highway:cycleway, cycleway:track
    private boolean walking = false; //foot, sidewalk, highway:footway
    private boolean car = false;
    private boolean onewayBicycle = false;
    private int maxspeed = 0;
    private String name;

    public Street(ArrayList<String> tags, int defaultSpeed) {

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
                case "cycleway:right":
                case "cycleway:left":
                    bicycle = true;
                    break;
                case "name":
                    name = value.intern();
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
            maxspeed = defaultSpeed;
        }
        tags.clear();
    }
    public boolean isOnewayCar() {
        return onewayCar;
    }

    public boolean isBicycle() {
        return bicycle;
    }

    public boolean isWalking() {
        return walking;
    }

    public boolean isCar() {
        return car;
    }

    public boolean isOnewayBicycle() {
        return onewayBicycle;
    }

    public int getMaxspeed() {
        return maxspeed;
    }

    public String getName() {
        return name;
    }
}
