package bfst.citiesAndStreets;

import bfst.OSMReader.Node;
import bfst.addressparser.Address;
import bfst.canvas.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class City implements Serializable, Comparable<City>, Drawable {
    private final String name;
    private final Node node;
    private final String cityType;

    private City(
            String _name,
            Node _node,
            String _cityType
    ) {
        name = _name;
        node = _node;
        cityType = _cityType;
    }

    @Override
    public int compareTo(City that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public void draw(GraphicsContext gc, double scale, boolean smartTrace) {
        gc.fillText(this.name, node.getLon(), node.getLat());
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
