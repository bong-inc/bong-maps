package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Type {

    UNKNOWN("", new String[]{""},Color.BLACK,1,false, Color.BLACK, 0.00001f, 5f),
    COASTLINE("natural",new String[]{"coastline"},Color.valueOf("#f0f0f0"),0,true, Color.valueOf("#f0f0f0"), 0.00001f, 5f),
    RESIDENTIAL("landuse",new String[]{"residential","industrial"},Color.valueOf("#e8e8e8"),0,true, Color.valueOf("#b3b3b3"), 0.001f, 0.5f),
    FARMFIELD("landuse",new String[]{"farmland"},Color.valueOf("#f7f6eb"),0,true, Color.valueOf("#f2ff00"), 0.05f, 5f),
    BEACH("natural",new String[]{"beach"},Color.BEIGE,0,true, Color.valueOf("#f8ff70"), 0.01f, 5f),
    FOREST("landuse",new String[]{"forest","meadow","grass"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 0.01f, 5f),
    NATURALS("natural",new String[]{"scrub","grassland","heath", "wood"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 0.03f, 5f),
    LEISURE("leisure",new String[]{"park"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 0.03f, 5f),
    WATER("natural",new String[]{"water"},Color.valueOf("#ade1ff"),0,true, Color.AQUA, 0.01f, 5f),
    WETLAND("natural",new String[]{"wetland"},Color.valueOf("#a5f2d6"),0,true, Color.valueOf("#ade1ff"), 0.03f, 5f),
    WATERWAY("waterway",new String[]{""},Color.valueOf("#ade1ff"),1,false, Color.AQUA, 0.05f, 5f),
    BRIDGE("man_made", new String[]{"bridge"}, Color.valueOf("#ebebeb"), 0, true, Color.ORANGE, 0.005f, 5f),
    PIER("man_made", new String[]{"pier"}, Color.DARKGREY, 1, false, Color.ORANGE, 0.3f, 5f),
    RAILWAY("railway",new String[]{"rail","light_rail","subway"}, Color.DARKGREY,1,false, Color.valueOf("#da21ff"), 0.0025f, 5f),
    BUILDING("building",new String[]{""},Color.valueOf("#dbdbdb"),0,true, Color.valueOf("#ff7c3b"), 0.5f, 5f),

    PRIMARY_ROAD("highway",new String[]{"primary","motorway","trunk"},Color.YELLOW,1.5,false, Color.YELLOW, 0.0005f, 5f),
    SECONDARY_ROAD("highway",new String[]{"secondary"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 0.005f, 5f),
    TERTIARY_ROAD("highway",new String[]{"tertiary"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 0.05f, 5f),
    OTHER("highway",new String[]{"residential", "unclassified", "track", "footway", "cycleway", "path", "service", "motorway_link", "steps"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 0.2f, 5f);


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
    private final float minMxx;
    private final float maxMxx;

    Type(String key, String[] value, Color color, double width, boolean shouldHaveFill, Color alternateColor, float minMxx, float maxMxx) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.width = width;
        this.shouldHaveFill = shouldHaveFill;
        this.shouldHaveStroke = (width != 0);
        this.alternateColor = alternateColor;
        this.minMxx = minMxx;
        this.maxMxx = maxMxx;
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

    public float getMinMxx() {
        return minMxx;
    }

    public float getMaxMxx() {
        return maxMxx;
    }
}