package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Pin {

    private float centerX;
    private float centerY;
    private float size;

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public Pin(float centerX, float centerY, float size) {
        this.centerX = centerX;
        this.centerY = centerY;
        //this.size = size;
    }

    public void draw(GraphicsContext gc) {
        draw(gc, 1);
    }

    public void draw(GraphicsContext gc, double size) {
        gc.setFill(Color.RED);
        gc.beginPath();
        String path = "c-9.9,0,-18,7.8,-18,17.4c0,3.8,1.3,7.4,3.6,10.4l13.6,17.8c0.3,0.4,1,0.5,1.4,0.2c0.1,-0.1,0.1,-0.1,0.2,-0.2l13.6,-17.8c2.3,-3,3.6,-6.6,3.6,-10.4c0,-9.6,-8.1,-17.4,-18,-17.4z" +
                "m-1.4,24.2c-3.8,-0.8,-6.3,-4.4,-5.5,-8.2c0.8,-3.8,4.4,-6.3,8.2,-5.5c2.8,0.6,5,2.7,5.5,5.5c0.7,3.8,-1.8,7.5,-5.5,8.2c-0.9,0.2,-1.8,0.2,-2.7,0z";
        double factor = size*0.6;
        String scaledPath = scaleSvgPath(path, factor);
        String translated = "M" + centerX + "," + (centerY - (45.97443389892578*factor)) + scaledPath;

        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();
    }

    public String scaleSvgPath(String text, double factor) {
        String out = "";
        String regex = "([Mmcl]{1}[0-9,\\-\\.]+)|(z)";

        return RouteInstructionIndicator.regexedString(text, factor, out, regex);
    }

}