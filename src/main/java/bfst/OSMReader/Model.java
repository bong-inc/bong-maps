package bfst.OSMReader;

import bfst.addressparser.Address;
import bfst.canvas.CanvasElement;
import bfst.canvas.City;
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

    private Map<Type, KDTree> kdtreeByType;
    private ArrayList<Address> addresses;
    private ArrayList<City> cities;
    private Graph graph;
    private KDTree addressKDTree;
    private KDTreeForEdges roadKDTree;
    private KDTree citiesKdTree;

    private Bound bound;

    public Model(OSMReader reader){
        this.bound = reader.getBound();
        this.addresses = reader.getAddresses();
        this.cities = reader.getCities();
        this.graph = reader.getGraph();
        Collections.sort(addresses);
        Collections.sort(cities);
        addresses.trimToSize();
        cities.trimToSize();
        this.kdtreeByType = createKdtreeByType(reader.getDrawableByType());
        this.addressKDTree = createKDTreeFromAddresses();
        this.roadKDTree = new KDTreeForEdges(reader.getRoadEdges(), new Range(bound.getMinLon(),bound.getMinLat(),bound.getMaxLon(), bound.getMaxLat()));
        this.citiesKdTree = createKDTreeFromCities();

        this.cities = null;
    }

    public Map<Type, KDTree> createKdtreeByType(Map<Type, ArrayList<CanvasElement>> drawablesByType){
        kdtreeByType = new HashMap<>();
        for(Entry<Type, ArrayList<CanvasElement>> e : drawablesByType.entrySet()){
            e.getValue().trimToSize();
            KDTree current = new KDTree(e.getValue(), new Range(bound.getMinLon(),bound.getMinLat(),bound.getMaxLon(), bound.getMaxLat()));
            kdtreeByType.put(e.getKey(), current);
        }
        return kdtreeByType;
    }

    public KDTree getKDTreeByType(Type type){
        return kdtreeByType.get(type);
    }

    private KDTree createKDTreeFromAddresses(){
        ArrayList<CanvasElement> list = new ArrayList<>(addresses);
        return new KDTree(list, new Range(bound.getMinLon(),bound.getMinLat(),bound.getMaxLon(), bound.getMaxLat()));
    }

    private KDTree createKDTreeFromCities(){
        ArrayList<CanvasElement> list = new ArrayList<>(cities);
        return new KDTree(list, new Range(bound.getMinLon(),bound.getMinLat(),bound.getMaxLon(), bound.getMaxLat()));
    }
    
    public ArrayList<Address> getAddresses(){
        return addresses;
    }

    public Bound getBound(){
        return bound;
    }

    public KDTree getCitiesKdTree(){
        return citiesKdTree;
    }

    public Graph getGraph() {
        return graph;
    }

    public KDTree getAddressKDTree() {
        return addressKDTree;
    }

    public KDTreeForEdges getRoadKDTree() {
        return roadKDTree;
    }
}
