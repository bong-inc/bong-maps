package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

public enum Type implements Serializable {

    UNKNOWN("","",Color.BLACK,1,false),
    COASTLINE("natural","coastline",Color.valueOf("#f0f0f0"),0,true),
    FARMFIELD("landuse","farmland",Color.valueOf("#f7f6eb"),0,true),
    BEACH("natural","beach",Color.BEIGE,0,true),
    WATER("natural","water",Color.valueOf("#ade1ff"),0,true),
    WATERWAY("waterway","",Color.valueOf("#ade1ff"),1,false),
    FOREST("landuse","forest",Color.valueOf("#c8f2bb"),0,true),
    RESIDENTIAL("landuse","residential",Color.valueOf("#e8e8e8"),0,true),
    LEISURE("leisure","park",Color.valueOf("#c8f2bb"),0,true),
    RAILWAY("railway","rail", Color.DARKGREY,1,false),
    BUILDING("building","",Color.valueOf("#dbdbdb"),0,true),
    PRIMARY_ROAD("highway","primary",Color.YELLOW,1.5,false),
    SECONDARY_ROAD("highway","secondary",Color.WHITE,1,false),
    TERTIARY_ROAD("highway","tertiary",Color.WHITE,1,false),
    RESIDENTIAL_ROAD("highway","residential",Color.WHITE,1,false);

    //FIXME bug when width=0

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
    private final boolean shouldHaveFill;
    private final boolean shouldHaveStroke;

    Type(String key, String value, Color color, double width, boolean shouldHaveFill) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.width = width;
        this.shouldHaveFill = shouldHaveFill;
        this.shouldHaveStroke = (width != 0);
    }


    public static Type[] getTypes(){
        return Type.values();
    }

    public double getWidth(){
        return width;
    }

    public boolean shouldHaveFill(){
        return shouldHaveFill;
    }

    public Paint getColor() {
        return color;
    }

    public boolean shouldHaveStroke() {
        return shouldHaveStroke;
    }
}