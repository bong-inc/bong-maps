package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.OSMReader.SortedArrayList;

import java.io.Serializable;
import java.util.ArrayList;

public class Graph implements Serializable {

    public SortedArrayList<Node> getNodes() {
        return nodes;
    }

    //private HashMap<Long,HashSet<Edge>> adj;

    private SortedArrayList<Node> nodes;

    public Graph() {
        nodes = new SortedArrayList<>();

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
        if (nodes.get(v) != null) {
            nodes.get(v).addEdge(edge);
        } else {
            Node newNode = edge.getTailNode();
            newNode.addEdge(edge);
            nodes.add(newNode);
        }
        if (nodes.get(w) != null) {
            nodes.get(w).addEdge(edge);
        } else {
            Node newNode = edge.getHeadNode();
            newNode.addEdge(edge);
            nodes.add(newNode);
        }

    }

    public void sortNodes() {
        nodes.sort();
    }

    public Iterable<Edge> edges() {
        ArrayList<Edge> list = new ArrayList<>();

        for (Node node : nodes) {
            list.addAll(node.getAdj());
        }

        return list;
    }

}
