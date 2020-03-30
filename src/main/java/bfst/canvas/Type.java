package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

public enum Type {

    UNKNOWN("", new String[]{""},Color.BLACK,1,false, Color.BLACK, 0.1000246664f),
    COASTLINE("natural",new String[]{"coastline"},Color.valueOf("#f0f0f0"),0,true, Color.valueOf("#f0f0f0"), 0.000005001233322f),
    RESIDENTIAL("landuse",new String[]{"residential","industrial"},Color.valueOf("#e8e8e8"),0,true, Color.valueOf("#b3b3b3"), 0.005001233322f),
    FARMFIELD("landuse",new String[]{"farmland"},Color.valueOf("#f7f6eb"),0,true, Color.valueOf("#f2ff00"), 0.01500369996f),
    BEACH("natural",new String[]{"beach"},Color.BEIGE,0,true, Color.valueOf("#f8ff70"), 0.02000493329f),
    FOREST("landuse",new String[]{"forest","meadow","grass"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 0.01500369996f),
    NATURALS("natural",new String[]{"scrub","grassland","heath"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 0.03000739993f),
    LEISURE("leisure",new String[]{"park"},Color.valueOf("#c8f2bb"),0,true, Color.valueOf("#04ff00"), 0.03000739993f),
    WATER("natural",new String[]{"water"},Color.valueOf("#ade1ff"),0,true, Color.AQUA, 0.01000246664f),
    WATERWAY("waterway",new String[]{""},Color.valueOf("#ade1ff"),1,false, Color.AQUA, 0.05001233322f),
    BRIDGE("man_made", new String[]{"bridge"}, Color.DARKGREY, 0, true, Color.ORANGE, 0.005001233322f),
    RAILWAY("railway",new String[]{"rail","light_rail","subway"}, Color.DARKGREY,1,false, Color.valueOf("#da21ff"), 0.002500616661f),
    BUILDING("building",new String[]{""},Color.valueOf("#dbdbdb"),0,true, Color.valueOf("#ff7c3b"), 0.1000246664f),

    PRIMARY_ROAD("highway",new String[]{"primary","motorway"},Color.YELLOW,1.5,false, Color.YELLOW, 0.00000500123f),
    SECONDARY_ROAD("highway",new String[]{"secondary"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 0.00500123f),
    TERTIARY_ROAD("highway",new String[]{"tertiary"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 0.0500123f),
    OTHER("highway",new String[]{"residential", "unclassified", "track", "footway", "cycleway", "path", "service", "motorway_link"},Color.WHITE,1,false, Color.valueOf("#6e7fff"), 0.12503075f);


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

    Type(String key, String[] value, Color color, double width, boolean shouldHaveFill, Color alternateColor, float minMxx) {
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

    public float getMinMxx() {
        return minMxx;
    }
}