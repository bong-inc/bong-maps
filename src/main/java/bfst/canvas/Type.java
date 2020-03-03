package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Type {

    UNKNOWN(Color.BLACK,1,false),
    BUILDING(Color.valueOf("#dbdbdb"),1,true),
    HIGHWAY(Color.valueOf("#ffffff"),1,false),
    COASTLINE(Color.valueOf("#f0f0f0"),1,true),
    WATER(Color.valueOf("#ade1ff"),1,true),
    GREEN(Color.valueOf("#c8f2bb"),1,true),
    PIN(Color.RED,1,true),
    BEACH(Color.BEIGE,1,true),
    FOREST(Color.valueOf("#c8f2bb"),1,true),
    WATERWAY(Color.valueOf("#ade1ff"),1,false),
    FARMFIELD(Color.BEIGE,1,true);

    private final Color color;
    private final double width;
    private final boolean fill;

    Type(Color color, double width, boolean fill) {
        this.color = color;
        this.width = width;
        this.fill = fill;
    }

    public double getWidth(){
        return width;
    }

    public boolean getFill(){
        return fill;
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