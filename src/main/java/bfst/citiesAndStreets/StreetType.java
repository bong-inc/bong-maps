package bfst.citiesAndStreets;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum StreetType {

    MOTORWAY(Color.YELLOW, 1.5, Color.BLUE, 1),
    PRIMARY(Color.YELLOW, 1.5, Color.BLUE, 1),
    SECONDARY(Color.WHITE, 1, Color.RED, 1000),
    TERTIARY(Color.WHITE, 1, Color.RED, 10000),
    OTHER(Color.WHITE, 1, Color.RED, 25000);

    private final Color color;
    private final double width;
    private final Color alternateColor;
    private final int minMxx;

StreetType(Color color, double width, Color alternateColor, int minMxx) {

    this.color = color;
    this.width = width;
    this.alternateColor = alternateColor;
    this.minMxx = minMxx;


}
    public Paint getColor() {
        return color;
    }

    public Paint getAlternateColor() {
        return alternateColor;
    }

    public int getMinMxx() {
    return minMxx;
    }

    public double getWidth() {
    return width;
    }

}
