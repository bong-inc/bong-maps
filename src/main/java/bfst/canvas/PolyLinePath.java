package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class PolyLinePath extends ArrayList<LinePath> implements Drawable {
    private static final long serialVersionUID = -4838798038938840050L;
    Type type;

    public PolyLinePath(ArrayList<ArrayList<Point>> currentRelation, Type type) {
        for (var way : currentRelation) {
            add(new LinePath(way, type));
        }
        this.type = type;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        for (var line : this) {
            line.trace(gc);
        }
        gc.stroke();
    }

    @Override
    public Type getType() {
        return type;
    }
}
