package bfst.routeFinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Dijkstra {

    private HashMap<Long, Double> distTo;
    private HashMap<Long, Edge> edgeTo;
    private HashMap<Long, Double> distTo2;
    private HashMap<Long, Edge> edgeTo2;
    private IndexMinPQ pq;
    private IndexMinPQ pq2;
    private int currDijkstra = 1;

    public long getLastNode() {
        return lastNode;
    }

    private long lastNode = 1;

    public Dijkstra(Graph G, long s, long t, String vehicle, boolean shortestRoute) {
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        distTo2 = new HashMap<>();
        edgeTo2 = new HashMap<>();


        distTo.put(s, 0.0);
        distTo2.put(t, 0.0);

        pq = new IndexMinPQ();
        pq.insert(s, distTo.get(s));

        pq2 = new IndexMinPQ();
        pq2.insert(t, distTo2.get(t));

        while (!pq.isEmpty()) {

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
            lastNode = determineRelax(currDijkstra, G, vehicle, shortestRoute);
        }

    }

    private long determineRelax(int currDijkstra, Graph G, String vehicle, boolean shortestRoute) {
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
                        if (currDijkstra == 1) {
                            relax(edge, v, shortestRoute, distTo, edgeTo, pq);
                        } else {
                            relax(edge, v, shortestRoute, distTo2, edgeTo2, pq2);
                        }
                    }
                    break;
                case "Walk":
                    if (edge.getStreet().isWalking()) {

                        if (currDijkstra == 1) {
                            relax(edge, v, true, distTo, edgeTo, pq);
                        } else {
                            relax(edge, v, true, distTo2, edgeTo2, pq2);
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
                        if (currDijkstra == 1) {
                            relax(edge, v, true, distTo, edgeTo, pq);
                        } else {
                            relax(edge, v, true, distTo2, edgeTo2, pq2);
                        }
                    }
                    break;
            }
        }

        if (currDijkstra == 1) {
            if (distTo2.containsKey(v)) {
                //distTo.putAll(distTo2);
                //edgeTo.putAll(edgeTo2);
                return v;
            }
        } else {
            if (distTo.containsKey(v)) {
                //distTo.putAll(distTo2);
                //edgeTo.putAll(edgeTo2);
                return v;
            }
        }
        return 1;
    }

    private void relax(Edge edge, long v, boolean shortestRoute, HashMap<Long, Double> distTo, HashMap<Long, Edge> edgeTo, IndexMinPQ pq) {
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

        if(distTo.get(w) > distTo.get(v) + edge.getWeight()) {
            distTo.put(w, distTo.get(v) + weight);
            edgeTo.put(w, edge);
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo.get(w));
            } else {
                pq.insert(w, distTo.get(w));
            }
        }
    }

    public boolean hasPathTo(long v, int currDijkstra) {
        if (currDijkstra == 1) {
            return distTo.containsKey(v);
        } else {
            return distTo2.containsKey(v);
        }
    }

    public HashMap<Long, Edge> getAllEdgeTo() {
        HashMap<Long, Edge> allEdgeTo = new HashMap<>();
        allEdgeTo.putAll(edgeTo);
        allEdgeTo.putAll(edgeTo2);
        return allEdgeTo;
    }



    public ArrayList<Edge> pathTo(long v, int currDijkstra) {
        if (!hasPathTo(v, currDijkstra)) {
            return null;
        }
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

}
