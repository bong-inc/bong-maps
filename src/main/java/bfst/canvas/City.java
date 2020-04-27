package bfst.canvas;

import bfst.OSMReader.Node;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.Serializable;

public class City extends CanvasElement implements Serializable, Comparable<City>, Drawable {
    public String getName() {
        return name;
    }

    private final String name;
    private final Node node;
    private final CityType type;
    private static boolean drawPrettyCitynames = true;

    private City(String _name, Node _node, String _cityType) {
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
        Font font = new Font(scale * type.getFontSize());
        gc.setFont(font);

        if (1/scale < type.getMaxMxx() && 1/scale > type.getMinMxx()) {
            if(drawPrettyCitynames){
                // performance heavy
                drawPretty(gc, scale);
            } else {
                drawNormal(gc, scale);
            }
        }
    }

    private void drawNormal(GraphicsContext gc, double scale){
        gc.fillText(this.name, node.getLon(), node.getLat());
    }

    private void drawPretty(GraphicsContext gc, double scale) {
        if (this.type == type.CITY) {
            gc.strokeText(this.name, node.getLon(), node.getLat() - 7 * scale);
            gc.fillText(this.name, node.getLon(), node.getLat() - 7 * scale);
            double radius = 4 * scale;
            gc.strokeOval(node.getLon() - (radius / 2), node.getLat() - (radius / 2), radius, radius);
            gc.fillOval(node.getLon() - (radius / 2), node.getLat() - (radius / 2), radius, radius);
        } else {
            gc.strokeText(this.name, node.getLon(), node.getLat());
            gc.fillText(this.name, node.getLon(), node.getLat());
        }
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

    @Override
    public Point2D getCentroid() {
        return new Point2D(node.getLon(), node.getLat());
    }

    @Override
    public Range getBoundingBox() {
        return new Range(node.getLon(), node.getLat(), node.getLon(), node.getLat());
    }

    @Override
    public void setBoundingBox() {
        // ignore
    }

    public static void setDrawPrettyCitynames(boolean drawPrettyCitynames){
        City.drawPrettyCitynames = drawPrettyCitynames;
    }

    public static boolean getDrawPrettyCitynames(){
        return drawPrettyCitynames;
    }
}
