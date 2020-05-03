package bfst.canvas;

import java.io.Serializable;

public class PointOfInterest implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private float lon;
    private float lat;
    private String name;

    public PointOfInterest(float lon, float lat, String name) {
        this.lon = lon;
        this.lat = lat;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " lon: " + lon + " lat: " + lat;
    }

    public String getName() {
        return name;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }
}
