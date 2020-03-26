package bfst.routeFinding;

import bfst.OSMReader.Model;

public class RouteFinder {

    public static Iterable<Street> getRoute(Graph graph, long startPoint, long endPoint, String vehicle) {

        Dijkstra dijkstra = new Dijkstra(graph, startPoint);
        return dijkstra.pathTo(endPoint);
    }

}
