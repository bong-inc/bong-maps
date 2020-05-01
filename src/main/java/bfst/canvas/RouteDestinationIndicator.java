package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteDestinationIndicator extends Pin{


    public RouteDestinationIndicator(float centerX, float centerY, float radius) {
        super(centerX,centerY,radius);
    }

    public void draw(GraphicsContext gc) {
        draw(gc, 1);
    }

    public void draw(GraphicsContext gc, double size) {
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

    String circlePath(float cx, float cy, float r) {
        return "M " + cx + " " + cy + " m -" + r + ", 0 a " + r + "," + r + " 0 1,0 " + (r * 2) + ",0 a " + r + "," + r + " 0 1,0 -" + (r * 2) + ",0";
    }

    public String scaleSvgPath(String text, double factor) {
        String out = "";
        String regex = "([Mmcl]{1}[0-9,\\-\\.]+)|(z)";

        return RouteInstructionIndicator.regexedString(text, factor, out, regex);
    }

}