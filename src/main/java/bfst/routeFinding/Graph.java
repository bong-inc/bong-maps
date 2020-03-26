package bfst.routeFinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph implements Serializable {


    public HashMap<Long, HashSet<Edge>> getAdj() {
        return adj;
    }

    private HashMap<Long,HashSet<Edge>> adj;


    public Graph() {
        adj = new HashMap<>();

    }

    public ArrayList<Long> getKeys() {
        ArrayList<Long> list = new ArrayList<>();
        for (Map.Entry<Long, HashSet<Edge>> entry : adj.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    public void addEdge(Edge edge) {
        long v = edge.getTailNode().getAsLong();
        long w = edge.getHeadNode().getAsLong();
        if (adj.containsKey(v)) {
            adj.get(v).add(edge);
        } else {
            adj.put(v, new HashSet<>());
            adj.get(v).add(edge);
        }
        if (adj.containsKey(w)) {
            adj.get(w).add(edge);
        } else {
            adj.put(w, new HashSet<>());
            adj.get(w).add(edge);
        }
    }

    public Iterable<Edge> edges() {
        ArrayList<Edge> list = new ArrayList<>();
        for (Map.Entry<Long,HashSet<Edge>> set : adj.entrySet()) {
            for (Edge edge : set.getValue()) {
                list.add(edge);
            }
        }
        return list;
    }

}
