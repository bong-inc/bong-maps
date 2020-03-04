package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Type {

    UNKNOWN("","",Color.BLACK,1,false),
    COASTLINE("natural","coastline",Color.valueOf("#f0f0f0"),1,true),
    FARMFIELD("landuse","farmland",Color.valueOf("#eff7e4"),1,true),
    BEACH("natural","beach",Color.BEIGE,1,true),
    WATER("natural","water",Color.valueOf("#ade1ff"),1,true),
    WATERWAY("waterway","",Color.valueOf("#ade1ff"),1,false),
    FOREST("landuse","meadow",Color.valueOf("#c8f2bb"),1,true),
    RAILWAY("railway","rail", Color.DARKGREY,1,false),
    BUILDING("building","",Color.valueOf("#dbdbdb"),1,true),
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
    }
}