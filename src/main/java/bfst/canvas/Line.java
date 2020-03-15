package bfst.canvas;

import java.io.Serializable;
import java.util.Scanner;
import javafx.scene.canvas.GraphicsContext;

public class Line implements Serializable, Drawable {
    private static final long serialVersionUID = 2919844445316377359L;
    double x1, y1, x2, y2;

    public Line(String line) {
        try (var scanner = new Scanner(line)) {
            scanner.next();
            x1 = scanner.nextDouble();
            y1 = scanner.nextDouble();
            x2 = scanner.nextDouble();
            y2 = scanner.nextDouble();
        }
    }

    public Line(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Line(Point from, Point to) {
        this(from.x, from.y, to.x, to.y);
	}

	public void draw(GraphicsContext gc, double scale) {
        gc.beginPath();
        gc.moveTo(x1, y1);
        gc.lineTo(x2, y2);
        gc.stroke();
    }

    @Override
    public Type getType() {
        return Type.UNKNOWN;
    }
}