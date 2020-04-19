package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.OSMReader.SortedArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Graph implements Serializable {

    public TreeMap<Long, ArrayList<Edge>> getAdj() {
        return adj;
    }

    public Node getNode(long id) {
        if (adj.get(id).get(0).getTailNode().getAsLong() == id) {
            return adj.get(id).get(0).getTailNode();
        } else {
            return adj.get(id).get(0).getHeadNode();
        }
    }


    public int getOutDegree(long id, String vehicle) {
        int count = 0;
        for (Edge edge : adj.get(id)) {
            if (vehicle.equals("Car")) {
                if (edge.getTailNode().getAsLong() == id || !edge.getStreet().isOnewayCar()) {
                    count++;
                }
            } else if(vehicle.equals("Bicycle")) {
                if (edge.getTailNode().getAsLong() == id || !edge.getStreet().isOnewayBicycle()) {
                    count++;
                }
            } else {
                if (edge.getTailNode().getAsLong() == id) {
                    count++;
                }
            }
        }
        return count;
    }

    private TreeMap<Long, ArrayList<Edge>> adj;

    public Graph() {
        adj = new TreeMap<>();
    }

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
