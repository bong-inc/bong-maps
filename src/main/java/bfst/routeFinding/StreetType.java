package bfst.routeFinding;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum StreetType {

    MOTORWAY(Color.YELLOW, 1.5, Color.BLUE, 0.00000500123f),
    PRIMARY(Color.YELLOW, 1.5, Color.BLUE, 0.00000500123f),
    SECONDARY(Color.WHITE, 1, Color.RED, 0.00500123f),
    TERTIARY(Color.WHITE, 1, Color.RED, 0.0500123f),
    OTHER(Color.WHITE, 1, Color.RED, 0.12503075f);

    private final Color color;
    private final double width;
    private final Color alternateColor;
    private final float minMxx;

StreetType(Color color, double width, Color alternateColor, float minMxx) {

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

    public float getMinMxx() {
    return minMxx;
    }

    public double getWidth() {
    return width;
    }

}
