package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    void draw(GraphicsContext gc, double scale);

    Type getType();
}
