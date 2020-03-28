package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.OSMReader.SortedArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph implements Serializable {

    public HashMap<Long, ArrayList<Edge>> getAdj() {
        return adj;
    }

    private HashMap<Long, ArrayList<Edge>> adj;

    public Graph() {
        adj = new HashMap<>();
    }
    /*
        public ArrayList<Long> getKeys() {
            ArrayList<Long> list = new ArrayList<>();
            for (Map.Entry<Long, HashSet<Edge>> entry : adj.entrySet()) {
                list.add(entry.getKey());
            }
            return list;
        }
    */
    public void addEdge(Edge edge) {
        long v = edge.getTailNode().getAsLong();
        long w = edge.getHeadNode().getAsLong();
        if (adj.containsKey(v)) {
            adj.get(v).add(edge);
        } else {
            ArrayList<Edge> list = new ArrayList<>();
            list.add(edge);
            adj.put(v, list);
        }
        if (adj.containsKey(w)) {
            adj.get(w).add(edge);
        } else {
            ArrayList<Edge> list = new ArrayList<>();
            list.add(edge);
            adj.put(w, list);
        }

    }



    public Iterable<Edge> edges() {
        ArrayList<Edge> list = new ArrayList<>();

        for (Map.Entry<Long, ArrayList<Edge>> entry : adj.entrySet()) {
            list.addAll(entry.getValue());
        }

        return list;
    }

}
