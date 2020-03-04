package bfst.canvas;

import bfst.OSMReader.Relation;
import bfst.OSMReader.Way;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

import java.io.Serializable;
import java.util.ArrayList;

public class PolyLinePath extends ArrayList<LinePath> implements Drawable, Serializable {
    private static final long serialVersionUID = -4838798038938840050L;
    Type type;

    public PolyLinePath(Relation relation, Type type) {
        ArrayList<Way> ways = relation.getWays();
        for (var way : ways) {
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
