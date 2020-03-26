package bfst.routeFinding;

public class RouteFinder {

    public static Iterable<Edge> getRoute(Graph graph, long startPoint, long endPoint, String vehicle) {

        Dijkstra dijkstra = new Dijkstra(graph, startPoint);
        return dijkstra.pathTo(endPoint);
    }

}
