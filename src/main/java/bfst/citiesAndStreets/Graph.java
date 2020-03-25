package bfst.citiesAndStreets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph {

    private final int V;

    public HashMap<Long, HashSet<Street>> getAdj() {
        return adj;
    }

    private HashMap<Long,HashSet<Street>> adj;


    public Graph(int V) {
        this.V = V;
        adj = new HashMap<>();

    }

    public int V() {
        return V;
    }

    public ArrayList<Long> getKeys() {
        ArrayList<Long> list = new ArrayList<>();
        for (Map.Entry<Long, HashSet<Street>> entry : adj.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    private void validateVertex(long v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    public void addEdge(Street street) {
        long v = street.getTailNode().getAsLong();
        long w = street.getHeadNode().getAsLong();
        validateVertex(v);
        validateVertex(w);
        if (adj.containsKey(v)) {
            adj.get(v).add(street);
        } else {
            adj.put(v, new HashSet<>());
            adj.get(v).add(street);
        }
        if (adj.containsKey(w)) {
            adj.get(w).add(street);
        } else {
            adj.put(w, new HashSet<>());
            adj.get(w).add(street);
        }
    }

    public Iterable<Street> edges() {
        ArrayList<Street> list = new ArrayList<>();
        for (Map.Entry<Long,HashSet<Street>> set : adj.entrySet()) {
            for (Street street : set.getValue()) {
                list.add(street);
            }
        }
        return list;
    }

}
