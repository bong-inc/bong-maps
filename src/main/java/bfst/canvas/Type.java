package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Type {

    UNKNOWN(Color.BLACK,1),
    BUILDING(Color.valueOf("#dbdbdb"),1),
    HIGHWAY(Color.valueOf("#ffffff"),1),
    COASTLINE(Color.RED,1),
    WATER(Color.valueOf("#ade1ff"),1),
    GREEN(Color.valueOf("#c8f2bb"),1),
    PIN(Color.RED,1),
    BEACH(Color.BEIGE,1),
    FOREST(Color.valueOf("#c8f2bb"),1),
    WATERWAY(Color.valueOf("#ade1ff"),1),
    FARMFIELD(Color.BROWN,1);

    private final Color color;
    private final double width;

    Type(Color color, double width) {
        this.color = color;
        this.width = width;
    }

    public double getWidth(){
        return width;
    }

    public Paint getColor() {
        return color;

        /*
        switch (type) {
            case WATER:
                return Color.valueOf("#ade1ff");
            case GREEN:
                return Color.valueOf("#c8f2bb");
            case BUILDING:
                return Color.valueOf("#dbdbdb");
            case HIGHWAY:
                return Color.valueOf("#ffffff");
            case PIN:
                return Color.RED;
            default:
                return Color.BLACK;
        }*/
    }
}