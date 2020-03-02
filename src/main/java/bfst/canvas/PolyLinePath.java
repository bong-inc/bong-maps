package bfst20.tegneprogram;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;

public class PolyLinePath extends ArrayList<LinePath> implements Drawable {
    private static final long serialVersionUID = -4838798038938840050L;
    Type type;

    public PolyLinePath(OSMRelation currentRelation, Type type) {
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
