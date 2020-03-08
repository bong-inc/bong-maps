package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public interface Drawable {
    void draw(GraphicsContext gc, double scale);

    Type getType();
}