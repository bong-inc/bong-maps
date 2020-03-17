package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

public enum Type implements Serializable {

    UNKNOWN("", new String[]{""},Color.BLACK,1,false, Color.BLACK, 20000),
    COASTLINE("natural",new String[]{"coastline"},Color.valueOf("#f0f0f0"),0,true, Color.valueOf("#f0f0f0"), 1),
    RESIDENTIAL("landuse",new String[]{"residential","industrial"},Color.valueOf("#e8e8e8"),0,true, Color.valueOf("#b3b3b3"), 1000),
    FARMFIELD("landuse",new String[]{"farmland"},Color.valueOf("#f7f6eb"),0,true, Color.valueOf("#f2ff00"), 3000),
    BEACH("natural",new String[]{"beach"},Color.BEIGE,0,true, Color.valueOf("#f8ff70"), 4000),
    FOREST("landuse",new String[]{"forest","meadow","grass"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 2000),
    NATURALS("natural",new String[]{"scrub","grassland","heath"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 6000),
    LEISURE("leisure",new String[]{"park"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 6000),
    WATER("natural",new String[]{"water"},Color.valueOf("#ade1ff"),0,true, Color.AQUA, 5000),
    WATERWAY("waterway",new String[]{""},Color.valueOf("#ade1ff"),1,false, Color.AQUA, 10000),
    RAILWAY("railway",new String[]{"rail","light_rail","subway"}, Color.DARKGREY,1,false, Color.valueOf("#da21ff"), 500),
    BUILDING("building",new String[]{""},Color.valueOf("#dbdbdb"),0,true, Color.valueOf("#ff7c3b"), 20000),
    PRIMARY_ROAD("highway",new String[]{"primary","motorway"},Color.YELLOW,1.5,false, Color.YELLOW, 1),
    SECONDARY_ROAD("highway",new String[]{"secondary"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 1000),
    TERTIARY_ROAD("highway",new String[]{"tertiary"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 10000),
    RESIDENTIAL_ROAD("highway",new String[]{"residential", "unclassified"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 25000);


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
    private final int minMxx;

    Type(String key, String[] value, Color color, double width, boolean shouldHaveFill, Color alternateColor, int minMxx) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.width = width;
        this.shouldHaveFill = shouldHaveFill;
        this.shouldHaveStroke = (width != 0);
        this.alternateColor = alternateColor;
        this.minMxx = minMxx;
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

    public int getMinMxx() {
        return minMxx;
    }
}