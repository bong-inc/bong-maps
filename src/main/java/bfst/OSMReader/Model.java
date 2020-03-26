package bfst.OSMReader;

import bfst.addressparser.Address;
import bfst.canvas.City;
import bfst.canvas.Drawable;
import bfst.canvas.Type;
import bfst.routeFinding.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Model implements Serializable {
    private static final long serialVerionUID = 101010101010100l;

    private Map<Type, ArrayList<Drawable>> drawablesByType;
    private ArrayList<Address> addresses;
    private ArrayList<City> cities;
    private Graph graph;
    private Dijkstra dijkstra;

    private ArrayList<Street> streets;
    private Bound bound;

    public Model(OSMReader reader){
        this.drawablesByType = reader.getDrawableByType();
        this.bound = reader.getBound();
        this.addresses = reader.getAddresses();
        this.cities = reader.getCities();
        this.streets = reader.getStreets();
        this.graph = reader.getGraph();
        Collections.sort(addresses);
        Collections.sort(cities);
        dijkstra = new Dijkstra(reader.getGraph(), 4927474144L);
    }

    public ArrayList<Drawable> getDrawablesOfType(Type type){
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
    public ArrayList<Street> getStreets() {
        return streets;
    }

    public Graph getGraph() {
        return graph;
    }

    public Dijkstra getDijkstra() {
        return dijkstra;
    }
}
