package bfst.routeFinding;

import bfst.OSMReader.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    private Graph graph = new Graph();

    private Node node0 = new Node(0, 0, 0);
    private Node node1 = new Node(1, 1, 0);
    private Node node2 = new Node(2, 1, 1);
    private Node node3 = new Node(3, 2, 3);
    private Node node4 = new Node(4, 4, 0);
    private Node node5 = new Node(5, 5, 5);
    private Node node6 = new Node(6, 6, 6);

    private Edge edge0;
    private Edge edge1;
    private Edge edge2;
    private Edge edge3;
    private Edge edge4;
    private Edge edge5;

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
        edge5 = new Edge(node5, node6, street);

        graph.addEdge(edge0);
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);

    }

    @Test
    void testDijkstra() {
        setVariables();
        Dijkstra dijkstra = new Dijkstra(graph, 4, 1, "Car", true);
        Assertions.assertEquals(1, dijkstra.getLastNode());
    }
/*
    @Test
    void testHasPathTo() {
        setVariables();
        Dijkstra dijkstra = new Dijkstra(graph, 5, 1, "Car", true);
    }

    @Test
    void testPathTo() {
        setVariables();
        Dijkstra dijkstra = new Dijkstra(graph, 4, 1, "Car", true);

        System.out.println(dijkstra.pathTo(dijkstra.getLastNode(), 2).size());
    }
*/
}