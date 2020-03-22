package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pin {

  float centerX, centerY, radius;

    public void changeRadius(float r){
        radius = r;
    }

    public Pin(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public void draw(GraphicsContext gc) {
        draw(gc, 1);
    }

    public void draw(GraphicsContext gc, double size) {
        System.out.println("pin draw!");
        gc.setFill(Color.RED);
        gc.beginPath();
        String path = "c-9.9,0,-18,7.8,-18,17.4c0,3.8,1.3,7.4,3.6,10.4l13.6,17.8c0.3,0.4,1,0.5,1.4,0.2c0.1,-0.1,0.1,-0.1,0.2,-0.2l13.6,-17.8c2.3,-3,3.6,-6.6,3.6,-10.4c0,-9.6,-8.1,-17.4,-18,-17.4z" +
                "m-1.4,24.2c-3.8,-0.8,-6.3,-4.4,-5.5,-8.2c0.8,-3.8,4.4,-6.3,8.2,-5.5c2.8,0.6,5,2.7,5.5,5.5c0.7,3.8,-1.8,7.5,-5.5,8.2c-0.9,0.2,-1.8,0.2,-2.7,0z";
        //String path = "l100,0l0,100l-100,0l0,-100z";
        double factor = size*0.6;
        String scaledPath = scaleSvgPath(path, factor);
        String translated = "M" + centerX + "," + (centerY - (45.97443389892578*factor)) + scaledPath;
        //String translated = "M" + centerX + "," + centerY + scaledPath;

        gc.appendSVGPath(translated);
        gc.closePath();
        gc.fill();
    }

    public String scaleSvgPath(String text, double factor) {
        String out = "";
        String regex = "([Mmcl]{1}[0-9,\\-\\.]+)|(z)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            String c = str.substring(0,1);

            String regex2 = "(-?\\d+\\.?\\d*)";
            Pattern pattern2 = Pattern.compile(regex2);
            Matcher matcher2 = pattern2.matcher(str);

            String out1 = c;
            while (matcher2.find()) {
                double d = Double.parseDouble(matcher2.group());
                d = d * factor;
                out1 += d + " ";
            }
            out += out1;
        }

        return out;
    }

}