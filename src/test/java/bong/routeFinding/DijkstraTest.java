package bong.routeFinding;

import bong.OSMReader.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class DijkstraTest {

    private Graph graph = new Graph();

    private Node node0 = new Node(0, 0, 0);
    private Node node1 = new Node(1, 1, 0);
    private Node node2 = new Node(2, 1, 1);
    private Node node3 = new Node(3, 2, 3);
    private Node node4 = new Node(4, 4, 0);
    private Node node5 = new Node(5, 5, 5);
    private Node node6 = new Node(6, 6, 6);
    private Node node7 = new Node(7, 7, 7);
    private Node node8 = new Node(8, 8, 8);
    private Node node9 = new Node(9, -1, -1);
    private Node node10 = new Node(10, 10, 10);
    private Node node11 = new Node(11, 11, 11);
    private Node node12 = new Node(12, 12, 12);

    private Edge edge0;
    private Edge edge1;
    private Edge edge2;
    private Edge edge3;
    private Edge edge4;
    private Edge edge5;
    private Edge edge6;
    private Edge edge7;
    private Edge edge8;
    private Edge edge9;
    private Edge edge10;

    private Street street;
    private Street bikeWalkStreet;


    public void setVariables() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("highway");
        tags.add("primary");
        street = new Street(tags, 80);

        ArrayList<String> oneWayTags = new ArrayList<>();
        oneWayTags.add("highway");
        oneWayTags.add("primary");
        oneWayTags.add("oneway");
        oneWayTags.add("");

        ArrayList<String> bikeWalkTags = new ArrayList<>();
        bikeWalkTags.add("highway");
        bikeWalkTags.add("path");
        bikeWalkStreet = new Street(bikeWalkTags, 50);

        Street oneWayStreet = new Street(oneWayTags, 80);
        edge0 = new Edge(node1, node0, street);
        edge1 = new Edge(node1, node2, street);
        edge2 = new Edge(node3, node1, oneWayStreet);
        edge3 = new Edge(node0, node2, street);
        edge4 = new Edge(node4, node1, street);
        edge5 = new Edge(node5, node6, street);
        edge6 = new Edge(node7, node4, street);
        edge7 = new Edge(node8, node7, street);
        edge8 = new Edge(node0, node9, street);
        edge9 = new Edge(node10, node11, bikeWalkStreet);
        edge10 = new Edge(node12, node11, bikeWalkStreet);

        graph.addEdge(edge0);
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);
        graph.addEdge(edge6);
        graph.addEdge(edge7);
        graph.addEdge(edge8);
        graph.addEdge(edge9);
        graph.addEdge(edge10);

    }

    @Test
    void testDijkstra() {
        try {
            setVariables();
            Dijkstra dijkstra = new Dijkstra(graph, 4, 1, "Car", true, true, true);
            Assertions.assertEquals(1, dijkstra.getLastNode());
        } catch (Exception e) {
            Assertions.fail();
        }

        Assertions.assertThrows(Exception.class, () -> {
            setVariables();
            new Dijkstra(graph, 0, 15, "Car", true, true, true);
        });

        try {
            setVariables();
            Dijkstra dijkstra = new Dijkstra(graph, 0, 4, "Bicycle", true, true, true);
            Assertions.assertEquals(0, dijkstra.getAllEdgeTo().size());
        } catch (Exception e) {
            Assertions.fail();
        }

        try {
            setVariables();
            Dijkstra dijkstra = new Dijkstra(graph, 0, 4, "Walk", true, true, true);
            Assertions.assertEquals(0, dijkstra.getAllEdgeTo().size());
        } catch (Exception e) {
            Assertions.fail();
        }

        try {
            setVariables();
            Dijkstra dijkstra = new Dijkstra(graph, 0, 4, "Walk", true, false, false);
            Assertions.assertEquals(0, dijkstra.getAllEdgeTo().size());
        } catch (Exception e) {
            Assertions.fail();
        }

        Node node01 = new Node(21,1,1);
        Node node02 = new Node(22,2,2);
        Node node03 = new Node(23,3,3);
        Node node04 = new Node(24,4,4);
        Node node05 = new Node(25,5,5);

        ArrayList<String> tags2 = new ArrayList<>();
        tags2.add("highway");
        tags2.add("primary");
        tags2.add("highway");
        tags2.add("cycleway");
        tags2.add("oneway");
        tags2.add("yes");
        street = new Street(tags2, 50);
        Edge edge1 = new Edge(node01, node02, street);
        Edge edge2 = new Edge(node02, node03, street);
        Edge edge3 = new Edge(node03, node04, street);
        Edge edge4 = new Edge(node04, node05, street);
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);

        try {
            Dijkstra dijkstra = new Dijkstra(graph, 21, 25, "Bicycle", true, true, true);
            long actual = dijkstra.getLastNode();
            Assertions.assertEquals(23, actual);


            Dijkstra dijkstra5 = new Dijkstra(graph, 25, 21, "Bicycle", true, true, true);
            Assertions.assertEquals(0, dijkstra5.getEdgeTo().size());
            Assertions.assertEquals(0, dijkstra5.getEdgeTo2().size());

        } catch (Exception e) {
            Assertions.fail();
        }

    }


    @Test
    void testPathTo() {
        try {
            setVariables();
            Dijkstra dijkstra = new Dijkstra(graph, 9, 8, "Car", true, true, true);

            ArrayList<Edge> part1 = dijkstra.pathTo(dijkstra.getLastNode(), 1);
            ArrayList<Edge> part2 = dijkstra.pathTo(dijkstra.getLastNode(), 2);

            Assertions.assertEquals(0, part1.get(0).getTailNode().getAsLong());
            Assertions.assertEquals(9, part1.get(0).getHeadNode().getAsLong());
            Assertions.assertEquals(1, part1.get(1).getTailNode().getAsLong());
            Assertions.assertEquals(0, part1.get(1).getHeadNode().getAsLong());

            Assertions.assertEquals(8, part2.get(0).getTailNode().getAsLong());
            Assertions.assertEquals(7, part2.get(0).getHeadNode().getAsLong());
            Assertions.assertEquals(7, part2.get(1).getTailNode().getAsLong());
            Assertions.assertEquals(4, part2.get(1).getHeadNode().getAsLong());
            Assertions.assertEquals(4, part2.get(2).getTailNode().getAsLong());
            Assertions.assertEquals(1, part2.get(2).getHeadNode().getAsLong());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void testDetermineRelax() {
        try {
            setVariables();
            Dijkstra dijkstra = new Dijkstra(graph, 9, 8, "Car", true, true, true);
            long actual = dijkstra.determineRelax(1, "Car", true, true, true);
            Assertions.assertEquals(1, actual);

            actual = dijkstra.determineRelax(2, "Car", true, true, true);
            Assertions.assertEquals(1, actual);

            actual = dijkstra.determineRelax(1, "Car", false, true, true);
            Assertions.assertEquals(1, actual);

            dijkstra = new Dijkstra(graph, 12, 10, "Bicycle", true, true, true);
            actual = dijkstra.determineRelax(1, "Bicycle", true, true, true);
            Assertions.assertEquals(11, actual);

            dijkstra = new Dijkstra(graph, 12, 10, "Walk", true, true, true);
            actual = dijkstra.determineRelax(1, "Walk", true, true, true);
            Assertions.assertEquals(11, actual);

            dijkstra = new Dijkstra(graph, 12, 10, "Bicycle", true, true, true);
            actual = dijkstra.determineRelax(2, "Bicycle", true, true, true);
            Assertions.assertEquals(12, actual);

            dijkstra = new Dijkstra(graph, 12, 10, "Walk", true, true, true);
            actual = dijkstra.determineRelax(2, "Walk", true, true, true);
            Assertions.assertEquals(12, actual);

            Dijkstra dijkstra1 = new Dijkstra(graph, 12, 10, "bruh", true, true, true);
            Assertions.assertThrows(Exception.class, () -> {
                dijkstra1.determineRelax(2, "bruh", true, true, true);
            });

            Node node13 = new Node(13, 6, 7);
            ArrayList<String> tags = new ArrayList<>();
            tags.add("highway");
            tags.add("cycleway");
            Street cycleStreet = new Street(tags, 50);

            Node node14 = new Node(14, 9, 9);
            Node node15 = new Node(15, 5, 0);

            graph.addEdge(new Edge(node7, node13, cycleStreet));

            tags.add("highway");
            tags.add("primary");
            tags.add("highway");
            tags.add("cycleway");
            tags.add("oneway");
            tags.add("yes");
            graph.addEdge(new Edge(node8, node14, new Street(tags, 50)));
            graph.addEdge(new Edge(node15, node4, new Street(tags, 50)));

            dijkstra = new Dijkstra(graph, 9, 14, "Car", true, true, true);
            actual = dijkstra.determineRelax(1, "Car", true, true, true);
            Assertions.assertEquals(1, actual);

        } catch (Exception e) {
            Assertions.fail();
        }

    }

}