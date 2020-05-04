package bong.routeFinding;

import bong.OSMReader.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class GraphTest {

    private Graph graph = new Graph();

    private Node node0 = new Node(0, 0, 0);
    private Node node1 = new Node(1, 1, 0);
    private Node node2 = new Node(2, 1, 1);
    private Node node3 = new Node(3, 2, 3);
    private Node node4 = new Node(4, 4, 0);

    private Edge edge0;
    private Edge edge1;
    private Edge edge2;
    private Edge edge3;
    private Edge edge4;

    private Street street;


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

        Street oneWayStreet = new Street(oneWayTags, 80);
        edge0 = new Edge(node1, node0, street);
        edge1 = new Edge(node1, node2, street);
        edge2 = new Edge(node3, node1, oneWayStreet);
        edge3 = new Edge(node0, node2, street);
        edge4 = new Edge(node4, node1, street);

        graph.addEdge(edge0);
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);

    }


    @Test
    public void testGetNode() {
        try {
            setVariables();
            Node actual = graph.getNode(2);
            Assertions.assertEquals(node2.getAsLong(), actual.getAsLong());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testGetOutDegree() {
        setVariables();
        int expected;
        int actual;

        expected = 4;
        actual = graph.getOutDegree(1, "Car");
        Assertions.assertEquals(expected, actual);

        expected = 4;
        actual = graph.getOutDegree(1, "Bicycle");
        Assertions.assertEquals(expected, actual);

        expected = 2;
        actual = graph.getOutDegree(2, "Car");
        Assertions.assertEquals(expected, actual);

        expected = 2;
        actual = graph.getOutDegree(2, "Walk");
        Assertions.assertEquals(expected, actual);

        expected = 1;
        actual = graph.getOutDegree(4, "Car");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testAddEdge() {
        setVariables();
        Graph thisGraph = graph;

        Edge edge = new Edge(node3, node4, street);
        thisGraph.addEdge(edge);

        long expected;
        long actual;

        expected = node3.getAsLong();
        actual = thisGraph.getAdj().get(3L).get(1).getTailNode().getAsLong();
        Assertions.assertEquals(expected, actual);

        expected = node4.getAsLong();
        actual = thisGraph.getAdj().get(4L).get(1).getHeadNode().getAsLong();
        Assertions.assertEquals(expected, actual);

        Node node5 = new Node(5, 5, 5);
        Node node6 = new Node(6, 6, 6);
        Edge aloneEdge = new Edge(node5, node6, street);
        thisGraph.addEdge(aloneEdge);

        expected = node5.getAsLong();
        actual = thisGraph.getAdj().get(5L).get(0).getTailNode().getAsLong();
        Assertions.assertEquals(expected, actual);

        expected = node6.getAsLong();
        actual = thisGraph.getAdj().get(6L).get(0).getHeadNode().getAsLong();
        Assertions.assertEquals(expected, actual);
    }

}