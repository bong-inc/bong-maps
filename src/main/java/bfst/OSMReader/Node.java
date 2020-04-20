package bfst.OSMReader;

import java.io.Serializable;
import java.util.function.LongSupplier;

public class Node implements LongSupplier, Serializable {
    private long id;
    private float lon;
    private float lat;
    private static final long serialVersionUID = 4076268921174823754L;

    public Node(long id, float lon, float lat){
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    @Override
    public long getAsLong() {
        return id;
    }

    @Override
    public String toString() {
        return "Node, lat:" + lat + " lon:" + lon + " ID:" + id;
    }
}
