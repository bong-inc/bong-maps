package bfst.canvas;

import bfst.OSMReader.Node;
import bfst.controllers.RouteController;
import bfst.routeFinding.Edge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class RouterControllertest {
    private RouteController routeController = new RouteController(new MapCanvas());

    @Test
    public void calculateTurnTest() {
        Edge prevEdge1 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), null);
        Edge currEdge1 = new Edge(new Node(2, 9, 10), new Node(2, 12, 5), null);

        double expected;
        double actual;
        expected = -104.03624346792648;
        actual = routeController.calculateTurn(prevEdge1, currEdge1);
        Assertions.assertEquals(expected, actual);

        Edge prevEdge2 = new Edge(new Node(1, 5, 6), new Node(2, 9, 10), null);
        Edge currEdge2 = new Edge(new Node(2, 9, 10), new Node(2, 7, 12), null);
        actual = routeController.calculateTurn(prevEdge2, currEdge2);
        expected = 90.0;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void timeStringTest() {
        String expected;
        String actual;

        routeController.setRouteTime(900.0);
        expected = 15 + " min";
        actual = routeController.timeString();
        Assertions.assertEquals(expected, actual);

        routeController.setRouteTime(9000.0);
        expected = 2 + " h " + 30 + " min";
        actual = routeController.timeString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void distanceStringTest() {
        String expected;
        String actual;

        routeController.setRouteDistance(101.1);
        expected = 101 + " m";
        actual = routeController.distanceString();
        Assertions.assertEquals(expected, actual);

        routeController.setRouteDistance(1011.1);
        expected = 1.01 + " km";
        actual = routeController.distanceString();
        Assertions.assertEquals(expected, actual);

        routeController.setRouteDistance(101234.56);
        expected = 101 + " km";
        actual = routeController.distanceString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void singleDirectRouteTest() {
        ArrayList<Edge> route = new ArrayList<>();

        Node node0 = new Node(0, 0, 0);
        Node node1 = new Node(1, 1, 1);
        Node node2 = new Node(2, 2, 2);
        Node node3 = new Node(3, 3, 3);
        Node node4 = new Node(4, 4, 4);


        route.add(new Edge(node1, node0, null));
        route.add(new Edge(node1, node2, null));
        route.add(new Edge(node3, node2, null));
        route.add(new Edge(node3, node4, null));

        ArrayList<Edge> actual = routeController.singleDirectRoute(route);

        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(i, actual.get(i).getTailNode().getLat());
        }
    }
    

}