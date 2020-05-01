package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    void draw(Drawer gc, double scale, boolean smartTrace);
}
