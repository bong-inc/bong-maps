package bfst.routeFinding;

import java.io.Serializable;
import java.util.ArrayList;

public class Street implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean onewayCar = false;
    private boolean bicycle = false;
    private boolean walking = false;
    private boolean car = false;
    private boolean onewayBicycle = false;
    private int maxspeed = 0;
    private String name;
    private Role role = Role.NO_ROLE;

    public Street(ArrayList<String> tags, int defaultSpeed) {

        for (int i = 0; i < tags.size(); i += 2) {
            String value = tags.get(i + 1);
            switch (tags.get(i)) {

                case "highway":
                    switch (value) {
                        case "footway":
                        case "steps":
                        case "pedestrian":
                        case "corridor":
                        case "crossing":
                            walking = true;
                            break;
                        case "cycleway":
                            bicycle = true;
                            role = Role.CYCLEWAY;
                            break;
                        case "path":
                            walking = true;
                            bicycle = true;
                            break;
                        case "motorway":
                            car = true;
                            role = Role.MOTORWAY;
                            break;
                        case "primary":
                        case "secondary":
                        case "tertiary":
                        case "trunk":
                        case "trunk_link":
                        case "primary_link":
                        case "secondary_link":
                        case "tertiary_link":
                            car = true;
                            break;
                        case "motorway_link":
                            car = true;
                            role = Role.MOTORWAY_LINK;
                            break;
                        case "residential":
                        case "living_street":
                        case "service":
                        case "track":
                        case "unclassified":
                            walking = true;
                            bicycle = true;
                            car = true;
                            break;
                        case "mini_roundabout":
                            onewayCar = true;
                            onewayBicycle = true;
                            role = Role.ROUNDABOUT;
                            break;

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
                    if (value.equals("yes")) {
                        onewayCar = true;

                        if (role == Role.CYCLEWAY) {
                            onewayBicycle = true;
                        }
                    }
                    break;
                case "oneway:bicycle":
                    onewayBicycle = true;
                    break;
                case "foot":
                    if (value.equals("yes") || value.equals("designated")) {
                        walking = true;
                    }
                    if (value.equals("private")) {
                        walking = false;
                    }
                    break;
                case "bicycle":
                    if (value.equals("yes") || value.equals("designated")) {
                        bicycle = true;
                    }
                    break;
                case "junction":
                    if (value.equals("roundabout")) {
                        onewayCar = true;
                        onewayBicycle = true;
                        role = Role.ROUNDABOUT;
                    }
                    break;
                case "service":
                    if (value.equals("parking_aisle") || value.equals("emergency_access")) {
                        car = false;
                        bicycle = false;
                        walking = false;
                    }
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

    public Role getRole() {
        return role;
    }

    public enum Role {
        NO_ROLE,
        MOTORWAY_LINK,
        ROUNDABOUT,
        MOTORWAY,
        CYCLEWAY
    }
}
