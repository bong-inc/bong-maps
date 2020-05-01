package bfst.canvas;

import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteOriginIndicator extends Indicator {

    public RouteOriginIndicator(float centerX, float centerY, float radius) {
        super(centerX,centerY,radius);
    }

    public void draw(Drawer gc) {
        draw(gc, 1);
    }

    public void draw(Drawer gc, double size) {
        double factor = size*0.6;

        gc.setFill(Color.BLACK);
        gc.beginPath();
        String translated = circlePath(super.getCenterX(),super.getCenterY(),(float) factor*12);
        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();

        gc.setFill(Color.WHITE);
        gc.beginPath();
        translated = circlePath(super.getCenterX(),super.getCenterY(),(float) factor*8);
        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();

    }

}