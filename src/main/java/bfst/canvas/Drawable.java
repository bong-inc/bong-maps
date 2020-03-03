package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public interface Drawable {
    void draw(GraphicsContext gc);

    Type getType();
}

enum Type {
    UNKNOWN,
    BUILDING,
    HIGHWAY,
    COASTLINE,
    WATER,
    GREEN,
    PIN;

    public static Paint getColor(Type type) {
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
        }
    }
}