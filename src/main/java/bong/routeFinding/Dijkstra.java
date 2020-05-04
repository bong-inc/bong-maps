package bong.routeFinding;

import bong.OSMReader.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Dijkstra {

    private HashMap<Long, Double> distTo;
    private HashMap<Long, Edge> edgeTo;
    private HashMap<Long, Double> distTo2;
    private HashMap<Long, Edge> edgeTo2;
    private IndexMinPQ<Double> pq;
    private IndexMinPQ<Double> pq2;
    private int currDijkstra = 1;
    private Node startNode;
    private Node endNode;
    private Graph G;

    public long getLastNode() {
        return lastNode;
    }

    private long lastNode = 1;

    public Dijkstra(Graph G, long s, long t, String vehicle, boolean shortestRoute, boolean useBidirectional, boolean useAStar) throws Exception {
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        distTo2 = new HashMap<>();
        edgeTo2 = new HashMap<>();

        this.G = G;

        startNode = G.getNode(s);
        endNode = G.getNode(t);

        distTo.put(s, 0.0);
        distTo2.put(t, 0.0);

        pq = new IndexMinPQ<>();
        pq.insert(s, distTo.get(s));

        pq2 = new IndexMinPQ<>();
        pq2.insert(t, distTo2.get(t));

        while (!pq.isEmpty()) {

            if (useBidirectional) {
                if (currDijkstra == 1) {
                    if (distTo2.containsKey(lastNode)) {
                        break;
                    }
                    currDijkstra = 2;
                } else {
                    if (distTo.containsKey(lastNode)) {
                        break;
                    }
                    currDijkstra = 1;
                }
            } else {
                if (distTo.containsKey(t)) {
                    lastNode = t;
                    break;
                }
            }
            lastNode = determineRelax(currDijkstra, vehicle, shortestRoute, useBidirectional, useAStar);
        }
    }

    public long determineRelax(int currDijkstra, String vehicle, boolean shortestRoute, boolean useBidirectional, boolean useAStar) throws Exception{
        long v;
        if (currDijkstra == 1) {
            v = pq.delMin();

        } else{
            v = pq2.delMin();
        }

        for (Edge edge : G.getAdj().get(v)) {
            switch (vehicle) {
                case "Car":
                    if (edge.getStreet().isCar()) {
                        if (edge.getStreet().isOnewayCar()) {
                            if ((currDijkstra == 1 && edge.getHeadNode().getAsLong() == v) || (currDijkstra == 2 && edge.getTailNode().getAsLong() == v)) {
                                break;
                            }
                        }
                        if (currDijkstra == 1 || !useBidirectional) {
                            relax(edge, v, shortestRoute, distTo, edgeTo, pq, useAStar);
                        } else {
                            relax(edge, v, shortestRoute, distTo2, edgeTo2, pq2, useAStar);
                        }
                    }
                    break;
                case "Walk":
                    if (edge.getStreet().isWalking()) {

                        if (currDijkstra == 1 || !useBidirectional) {
                            relax(edge, v, true, distTo, edgeTo, pq, useAStar);
                        } else {
                            relax(edge, v, true, distTo2, edgeTo2, pq2, useAStar);
                        }
                    }
                    break;
                case "Bicycle":
                    if (edge.getStreet().isBicycle()) {
                        if (edge.getStreet().isOnewayBicycle()) {
                            if ((currDijkstra == 1 && edge.getHeadNode().getAsLong() == v) || (currDijkstra == 2 && edge.getTailNode().getAsLong() == v)) {
                                break;
                            }
                        }
                        if (currDijkstra == 1 || !useBidirectional) {
                            relax(edge, v, true, distTo, edgeTo, pq, useAStar);
                        } else {
                            relax(edge, v, true, distTo2, edgeTo2, pq2, useAStar);
                        }
                    }
                    break;
            }
        }

        if (currDijkstra == 1) {
            if (distTo2.containsKey(v)) {
                return v;
            }
        } else {
            if (distTo.containsKey(v)) {
                return v;
            }
        }
        return 1;
    }

    private void relax(Edge edge, long v, boolean shortestRoute, HashMap<Long, Double> distTo, HashMap<Long, Edge> edgeTo, IndexMinPQ<Double> pq, boolean useAStar) throws Exception {
        long w = edge.other(v);
        if (!distTo.containsKey(w)) {
            distTo.put(w, Double.POSITIVE_INFINITY);
        }

        double distanceToDestination = 0;
        if (useAStar) {
            Node currNode = G.getNode(w);
            if (currDijkstra == 1) {
                distanceToDestination = Math.sqrt(Math.pow(endNode.getLat() - currNode.getLat(), 2) + Math.pow(endNode.getLon() - currNode.getLon(), 2));
            } else {
                distanceToDestination = Math.sqrt(Math.pow(startNode.getLat() - currNode.getLat(), 2) + Math.pow(startNode.getLon() - currNode.getLon(), 2));
            }
        }

        double weight;
        if (shortestRoute) {
            weight = edge.getWeight();
        } else {
            weight = edge.getWeight() / edge.getStreet().getMaxspeed();
            distanceToDestination = 0;
        }


        if(distTo.get(w) > distTo.get(v) + edge.getWeight()) {
            distTo.put(w, distTo.get(v) + weight);
            edgeTo.put(w, edge);
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo.get(w) + distanceToDestination);
            } else {
                pq.insert(w, distTo.get(w) + distanceToDestination);
            }
        }
    }

    public HashMap<Long, Edge> getAllEdgeTo() {
        HashMap<Long, Edge> allEdgeTo = new HashMap<>();
        allEdgeTo.putAll(edgeTo);
        allEdgeTo.putAll(edgeTo2);
        return allEdgeTo;
    }



    public ArrayList<Edge> pathTo(long v, int currDijkstra) {
        HashMap<Long, Edge> thisEdgeTo;
        if (currDijkstra == 1) {
            thisEdgeTo = edgeTo;
        } else {
            thisEdgeTo = edgeTo2;
        }

        Stack<Edge> path = new Stack<>();
        long x = v;
        for (Edge edge = thisEdgeTo.get(v); edge != null; edge = thisEdgeTo.get(x)) {

            path.push(edge);

            x = edge.other(x);
        }

        ArrayList<Edge> list = new ArrayList<>();

        while(!path.isEmpty()) {
            list.add(path.pop());
        }

        return list;
    }

    public HashMap<Long, Edge> getEdgeTo() {
        return edgeTo;
    }

    public HashMap<Long, Edge> getEdgeTo2() {
        return edgeTo2;
    }
}
