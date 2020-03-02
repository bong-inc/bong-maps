package bfst.OSMReader;


import javafx.geometry.Point2D;

public class Bound{
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

    public double getMinLat(){return minLat;}
    public double getMaxLat(){return maxLat;}
    public double getMinLon(){return minLon;}
    public double getMaxLon(){return maxLon;}
}