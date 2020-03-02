package bfst20.tegneprogram;

import javafx.scene.canvas.GraphicsContext;

public class LinePath implements Drawable {
    float[] coords;
    Type type;

    public LinePath(OSMWay way, Type type) {
        coords = new float[way.size() * 2];
        for (int i = 0 ; i < way.size() ; ++i) {
            coords[i*2] = way.get(i).lon;
            coords[i*2+1] = way.get(i).lat;
        }
        this.type = type;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }

    @Override
    public Type getType() {
        return type;
    }

    public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2 ; i < coords.length ; i += 2) {
            gc.lineTo(coords[i], coords[i+1]);
        }
    }

}