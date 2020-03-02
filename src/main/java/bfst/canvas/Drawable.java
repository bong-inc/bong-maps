package bfst20.tegneprogram;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    void draw(GraphicsContext gc);

    Type getType();
}
