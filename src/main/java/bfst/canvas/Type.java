package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

public enum Type implements Serializable {

    UNKNOWN("", new String[]{""},Color.BLACK,1,false),
    COASTLINE("natural",new String[]{"coastline"},Color.valueOf("#f0f0f0"),0,true),
    FARMFIELD("landuse",new String[]{"farmland"},Color.valueOf("#f7f6eb"),0,true),
    BEACH("natural",new String[]{"beach"},Color.BEIGE,0,true),
    WATER("natural",new String[]{"water"},Color.valueOf("#ade1ff"),0,true),
    WATERWAY("waterway",new String[]{""},Color.valueOf("#ade1ff"),1,false),
    FOREST("landuse",new String[]{"forest","meadow"},Color.valueOf("#c8f2bb"),0,true), //TODO meadow virker ikke
    NATURALS("natural",new String[]{"scrub","grassland","heath","wetland"},Color.valueOf("#c8f2bb"),0,true),
    RESIDENTIAL("landuse",new String[]{"residential","industrial"},Color.valueOf("#e8e8e8"),0,true),
    LEISURE("leisure",new String[]{"park"},Color.valueOf("#c8f2bb"),0,true),
    RAILWAY("railway",new String[]{"rail"}, Color.DARKGREY,1,false),
    BUILDING("building",new String[]{""},Color.valueOf("#dbdbdb"),0,true),
    PRIMARY_ROAD("highway",new String[]{"primary"},Color.YELLOW,1.5,false),
    SECONDARY_ROAD("highway",new String[]{""},Color.WHITE,1,false);
    //TERTIARY_ROAD("highway",new String[]{"tertiary"},Color.WHITE,1,false),
    //RESIDENTIAL_ROAD("highway",new String[]{"residential"},Color.WHITE,1,false);


    public String getKey() {
        return key;
    }

    public String[] getValue() {
        return value;
    }

    private final String key;
    private final String[] value;
    private final Color color;
    private final double width;
    private final boolean shouldHaveFill;
    private final boolean shouldHaveStroke;

    Type(String key, String[] value, Color color, double width, boolean shouldHaveFill) {
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