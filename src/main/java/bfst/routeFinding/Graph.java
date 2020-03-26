package bfst.routeFinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph {


    public HashMap<Long, HashSet<Street>> getAdj() {
        return adj;
    }

    private HashMap<Long,HashSet<Street>> adj;


    public Graph() {
        adj = new HashMap<>();

    }

    public ArrayList<Long> getKeys() {
        ArrayList<Long> list = new ArrayList<>();
        for (Map.Entry<Long, HashSet<Street>> entry : adj.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    public void addEdge(Street street) {
        long v = street.getTailNode().getAsLong();
        long w = street.getHeadNode().getAsLong();
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
