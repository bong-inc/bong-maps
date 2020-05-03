package bfst.canvas;

import javafx.scene.paint.Color;

public class RouteDestinationIndicator extends Indicator {

    public RouteDestinationIndicator(float centerX, float centerY, float radius) {
        super(centerX,centerY,radius);
    }

    public void draw(Drawer gc) {
        draw(gc, 1);
    }

    public void draw(Drawer gc, double size) {
        double factor = size*0.6;

        gc.setFill(Color.RED);
        gc.beginPath();
        String translated = circlePath(super.getCenterX(),super.getCenterY(),(float) factor*15);
        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();

        gc.setFill(Color.WHITE);
        gc.beginPath();
        translated = circlePath(super.getCenterX(),super.getCenterY(),(float) factor*10);
        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();

        gc.setFill(Color.RED);
        gc.beginPath();
        translated = circlePath(super.getCenterX(),super.getCenterY(),(float) factor*5);
        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();
    }

}