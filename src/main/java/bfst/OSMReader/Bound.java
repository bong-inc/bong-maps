package bfst.OSMReader;

import bfst.canvas.Drawable;
import bfst.canvas.Drawer;

import java.io.Serializable;

public class Bound implements Drawable, Serializable {
    private float minLat;
    private float maxLat;
    private float minLon;
    private float maxLon;
    private static final long serialVersionUID = 2L;

    public Bound(float minLat, float maxLat, float minLon, float maxLon){
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
    }

    public float getMinLat(){return minLat;}
    public float getMaxLat(){return maxLat;}
    public float getMinLon(){return minLon;}
    public float getMaxLon(){return maxLon;}

    public void draw(Drawer gc, double scale, boolean smartTrace){
        gc.beginPath();
        gc.setLineWidth(scale);
        gc.moveTo(minLon,minLat);
        gc.lineTo(minLon, maxLat);
        gc.lineTo(maxLon, maxLat);
        gc.lineTo(maxLon, minLat);
        gc.lineTo(minLon, minLat);
        gc.stroke();
    }

}