package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

public enum Type implements Serializable {

    UNKNOWN("", new String[]{""},Color.BLACK,1,false, Color.BLACK),
    COASTLINE("natural",new String[]{"coastline"},Color.valueOf("#f0f0f0"),0,true, Color.valueOf("#f0f0f0")),
    RESIDENTIAL("landuse",new String[]{"residential","industrial"},Color.valueOf("#e8e8e8"),0,true, Color.valueOf("#b3b3b3")),
    FARMFIELD("landuse",new String[]{"farmland"},Color.valueOf("#f7f6eb"),0,true, Color.valueOf("#f2ff00")),
    BEACH("natural",new String[]{"beach"},Color.BEIGE,0,true, Color.valueOf("#f8ff70")),
    FOREST("landuse",new String[]{"forest","meadow","grass"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00")),
    NATURALS("natural",new String[]{"scrub","grassland","heath"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00")),
    LEISURE("leisure",new String[]{"park"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00")),
    WATER("natural",new String[]{"water"},Color.valueOf("#ade1ff"),0,true, Color.AQUA),
    WATERWAY("waterway",new String[]{""},Color.valueOf("#ade1ff"),1,false, Color.AQUA),
    RAILWAY("railway",new String[]{"rail","light_rail","subway"}, Color.DARKGREY,1,false, Color.valueOf("#da21ff")),
    BUILDING("building",new String[]{""},Color.valueOf("#dbdbdb"),0,true, Color.valueOf("#ff7c3b")),
    PRIMARY_ROAD("highway",new String[]{"primary","motorway"},Color.YELLOW,1.5,false, Color.YELLOW),
    SECONDARY_ROAD("highway",new String[]{"secondary"},Color.WHITE,1,false, Color.valueOf("#6e7fff")),
    TERTIARY_ROAD("highway",new String[]{"tertiary"},Color.WHITE,1,false, Color.valueOf("#6e7fff")),
    RESIDENTIAL_ROAD("highway",new String[]{"residential"},Color.WHITE,1,false, Color.valueOf("#6e7fff"));


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
    private final Color alternateColor;

    Type(String key, String[] value, Color color, double width, boolean shouldHaveFill, Color alternateColor) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.width = width;
        this.shouldHaveFill = shouldHaveFill;
        this.shouldHaveStroke = (width != 0);
        this.alternateColor = alternateColor;
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

    public Paint getAlternateColor() {
        return alternateColor;
    }

    public boolean shouldHaveStroke() {
        return shouldHaveStroke;
    }
}