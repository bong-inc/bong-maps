package bfst.OSMReader;

import bfst.addressparser.Address;
import bfst.canvas.City;
import bfst.canvas.Drawable;
import bfst.canvas.Range;
import bfst.canvas.Type;
import bfst.routeFinding.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Model implements Serializable {
    private static final long serialVerionUID = 101010101010100l;

    private Map<Type, ArrayList<CanvasElement>> drawablesByType;
    private ArrayList<CanvasElement> coastLines;
    private Map<Type, KDTree> kdtreeByType;
    private ArrayList<Address> addresses;
    private ArrayList<City> cities;
    private Graph graph;

    private Bound bound;

    public Model(OSMReader reader){
        this.drawablesByType = reader.getDrawableByType();
        this.bound = reader.getBound();
        this.addresses = reader.getAddresses();
        this.cities = reader.getCities();
        this.graph = reader.getGraph();
        Collections.sort(addresses);
        Collections.sort(cities);
        this.coastLines = getDrawablesOfType(Type.COASTLINE);
        this.kdtreeByType = createKdtreeByType();
        System.out.println("model!");
    }

    public Map<Type, KDTree> createKdtreeByType(){
        kdtreeByType = new HashMap<Type, KDTree>();
        for(Entry<Type, ArrayList<CanvasElement>> e : drawablesByType.entrySet()){
            if(e.getKey() == Type.COASTLINE) continue;
            KDTree current = new KDTree(e.getValue(), new Range(bound.getMinLon(),bound.getMinLat(),bound.getMaxLon(), bound.getMaxLat()));
            kdtreeByType.put(e.getKey(), current);
        }
        return kdtreeByType;
    }

    public ArrayList<CanvasElement> getCoastLines(){
        return coastLines;
    }

    public KDTree getKDTreeByType(Type type){
        return kdtreeByType.get(type);
    }

    public ArrayList<CanvasElement> getDrawablesOfType(Type type){
        return drawablesByType.get(type);
    }

    public ArrayList<Address> getAddresses(){
        return addresses;
    }

    public Bound getBound(){
        return bound;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public Graph getGraph() {
        return graph;
    }
}
