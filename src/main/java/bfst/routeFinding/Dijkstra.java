package bfst.routeFinding;

import java.util.HashMap;
import java.util.Stack;

public class Dijkstra {

    private HashMap<Long, Double> distTo;
    private HashMap<Long, Edge> edgeTo;
    private String vehicle;
    private IndexMinPQ pq;

    public Dijkstra(Graph G, long s, String vehicle, boolean shortestRoute) {
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        this.vehicle = vehicle;

        distTo.put(s, 0.0);

        pq = new IndexMinPQ();
        pq.insert(s, distTo.get(s));

        while (!pq.isEmpty()) {
            long v = pq.delMin();
            for (Edge edge : G.getAdj().get(v)) {

                switch (vehicle) {
                    case "Car":
                        if (edge.getStreet().isCar()) {
                            if (edge.getStreet().isOnewayCar() && edge.getHeadNode().getAsLong() == v) {
                                break;
                            }
                            relax(edge, v, shortestRoute);
                        }
                        break;
                    case "Walk":
                        if (edge.getStreet().isWalking()) {

                            relax(edge, v, true);
                        }
                        break;
                    case "Bicycle":
                        if (edge.getStreet().isBicycle()) {
                            if (edge.getStreet().isOnewayBicycle() && edge.getHeadNode().getAsLong() == v) {
                                break;
                            }
                            relax(edge, v, true);
                        }
                        break;
                }
            }
        }
    }

    private void relax(Edge edge, long v, boolean shortestRoute) {
        long w = edge.other(v);
        if (!distTo.containsKey(w)) {
            distTo.put(w, Double.POSITIVE_INFINITY);
        }

        double weight;
        if (shortestRoute) {
            weight = edge.getWeight();
        } else {
            weight = edge.getWeight() / edge.getStreet().getMaxspeed();
        }

        if(distTo.get(w) >
                distTo.get(v) +
                        edge.getWeight()) {
            distTo.put(w, distTo.get(v) + weight);
            edgeTo.put(w, edge);
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo.get(w));
            } else {
                pq.insert(w, distTo.get(w));
            }
        }
    }

    public boolean hasPathTo(long v) {
        return distTo.containsKey(v);
    }

    public Iterable<Edge> pathTo(long v) {
        if (!hasPathTo(v)) {
            System.out.println("NULLLLL");
            return null;
        }
        Stack<Edge> path = new Stack<>();
        long x = v;
        for (Edge edge = edgeTo.get(v); edge != null; edge = edgeTo.get(x)) {

            path.push(edge);


            x = edge.other(x);
        }
        return path;
    }

}
