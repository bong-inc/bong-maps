package bfst.OSMReader;


import bfst.canvas.Drawable;
import bfst.canvas.Type;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

public class Bound implements Drawable,Serializable {
    private float minLat;
    private float maxLat;
    private float minLon;
    private float maxLon;

    public Bound(float minLat, float maxLat, float minLon, float maxLon){
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
    }
    public Bound(Point2D maxs, Point2D mins){
        this.minLat = (float) mins.getX();
        this.maxLat = (float) maxs.getX();
        this.minLon = (float) mins.getY();
        this.maxLon = (float) maxs.getY();
    }

    public float getMinLat(){return minLat;}
    public float getMaxLat(){return maxLat;}
    public float getMinLon(){return minLon;}
    public float getMaxLon(){return maxLon;}

    public void draw(GraphicsContext gc, double scale){
        gc.beginPath();
        gc.moveTo(minLon,minLat);
        gc.lineTo(minLon, maxLat);
        gc.lineTo(maxLon, maxLat);
        gc.lineTo(maxLon, minLat);
        gc.lineTo(minLon, minLat);
        gc.stroke();
    }

    public Type getType(){
        return Type.UNKNOWN;
    }
}