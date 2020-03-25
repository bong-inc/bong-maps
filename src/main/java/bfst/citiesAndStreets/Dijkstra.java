package bfst.citiesAndStreets;

import java.util.HashMap;
import java.util.Stack;

public class Dijkstra {

    private HashMap<Long, Double> distTo;
    private HashMap<Long, Street> edgeTo;
    private IndexMinPQ pq;

    public Dijkstra(Graph G, long s) {
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();

        distTo.put(s, 0.0);

        pq = new IndexMinPQ();
        pq.insert(s, distTo.get(s));

        while (!pq.isEmpty()) {
            long v = pq.delMin();
            for (Street street : G.getAdj().get(v)) {
                relax(street, v);
            }
        }
    }

    private void relax(Street street, long v) {
        long w = street.other(v);
        if(distTo.get(w) >
                distTo.get(v) +
                        street.getWeight()) {
            distTo.put(w, distTo.get(v) + street.getWeight());
            edgeTo.put(w, street);
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

    public Iterable<Street> pathTo(long v) {
        if (!hasPathTo(v)) return null;
        Stack<Street> path = new Stack<>();
        long x = v;
        for (Street street = edgeTo.get(v); street != null; street = edgeTo.get(x)) {
            path.push(street);
            x = street.other(x);
        }
        return path;
    }

}
