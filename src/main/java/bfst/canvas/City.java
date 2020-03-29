package bfst.canvas;

import bfst.OSMReader.Node;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

public class City implements Serializable, Comparable<City>, Drawable {
    public String getName() {
        return name;
    }

    private final String name;
    private final Node node;
    private final CityType type;

    private City(
            String _name,
            Node _node,
            String _cityType
    ) {
        name = _name;
        node = _node;
        switch (_cityType) {
            case "city":
                type = CityType.CITY;
                break;
            case "town":
                type = CityType.TOWN;
                break;
            case "hamlet":
                type = CityType.HAMLET;
                break;
            default:
                type = CityType.OTHER;
                break;
        }
    }

    @Override
    public int compareTo(City that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        gc.fillText(this.name, node.getLon(), node.getLat());
    }

    public CityType getType() {
        return type;
    }


    public static class Builder {
        private String name;
        private Node node;
        private String cityType;

        public Builder name(String _name) {
            name = _name;
            return this;
        }

        public Builder node(Node _node) {
            node = _node;
            return this;
        }

        public Builder cityType(String _cityType) {
            cityType = _cityType;
            return this;
        }

        public City build() {
            return new City(name, node, cityType);
        }
    }
}
