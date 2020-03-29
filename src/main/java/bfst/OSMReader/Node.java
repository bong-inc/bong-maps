package bfst.OSMReader;

import bfst.routeFinding.Edge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.LongSupplier;

public class Node implements LongSupplier, Serializable {
    private long id;
    private float lon;
    private float lat;

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
}
