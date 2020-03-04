package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;

public enum Type {

    UNKNOWN("","",Color.BLACK,1,false),
    BUILDING("building","",Color.valueOf("#dbdbdb"),1,true),
    HIGHWAY("highway","",Color.valueOf("#ffffff"),1,false),
    COASTLINE("natural","coastline",Color.valueOf("#f0f0f0"),1,true),
    WATER("natural","water",Color.valueOf("#ade1ff"),1,true),
    //GREEN(Color.valueOf("#c8f2bb"),1,true),
    //PIN(Color.RED,1,true),
    BEACH("natural","beach",Color.BEIGE,1,true),
    FOREST("landuse","meadow",Color.valueOf("#c8f2bb"),1,true),
    WATERWAY("waterway","",Color.valueOf("#ade1ff"),1,false),
    FARMFIELD("landuse","farmland",Color.BEIGE,1,true);

    private final String key;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    private final String value;
    private final Color color;
    private final double width;
    private final boolean fill;

    Type(String key, String value, Color color, double width, boolean fill) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.width = width;
        this.fill = fill;
    }


    public static Type[] getTypes(){
        return Type.values();
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