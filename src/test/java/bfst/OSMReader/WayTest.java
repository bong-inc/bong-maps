package bfst.OSMReader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WayTest {

    @Test
    public void wayTest(){
        Node nd1 = new Node(1, 10, 10);
        Node nd2 = new Node(2, 20, 20);
        Node nd3 = new Node(3, 99, 22);

        Way way1 = new Way(1);
        assertEquals(1, way1.getAsLong());

        way1.addNode(nd2.getAsLong());
        assertEquals(nd2.getAsLong(), way1.first());
        way1.addNodeToFront(nd1.getAsLong());
        assertEquals(nd1.getAsLong(), way1.first());
        way1.addNode(nd3.getAsLong());
        assertEquals(nd3.getAsLong(), way1.last());

        way1.trim();
        assertEquals(3, way1.getNodes().length);
    }

    Node nd1 = new Node(1, 1, 1);
    Node nd2 = new Node(2, 2, 2);
    Node nd3 = new Node(3, 3, 3);
    Node nd4 = new Node(4, 4, 4);

    @Test
    public void MergeTestFirstToFirst(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        w1.trim();
        Way w2 = new Way();
        w2.addNode(nd1.getAsLong());
        w2.addNode(nd3.getAsLong());
        w2.trim();

        test = Way.merge(w1, w2);
        assertEquals(2, test.first());
        assertEquals(3, test.last());
    }

    @Test
    public void MergeTestLastToLast(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        w1.trim();
        Way w2 = new Way();
        w2.addNode(nd3.getAsLong());
        w2.addNode(nd2.getAsLong());
        w2.trim();

        test = Way.merge(w1, w2);
        assertEquals(1, test.first());
        assertEquals(3, test.last());
    }

    @Test
    public void MergeTestFirstToLast(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        w1.trim();
        Way w2 = new Way();
        w2.addNode(nd3.getAsLong());
        w2.addNode(nd1.getAsLong());
        w2.trim();

        test = Way.merge(w1, w2);
        assertEquals(3, test.first());
        assertEquals(2, test.last());
    }

    @Test
    public void MergeTestLastToFirst(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        w1.trim();
        Way w2 = new Way();
        w2.addNode(nd2.getAsLong());
        w2.addNode(nd3.getAsLong());
        w2.trim();

        test = Way.merge(w1, w2);
        assertEquals(3, test.last());
        assertEquals(1, test.first());
    }

    @Test
    public void MergeTestUnconnected(){
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        Way w2 = new Way();
        w2.addNode(nd3.getAsLong());
        w2.addNode(nd4.getAsLong());

        String message = "";
        try {
            Way.merge(w1, w2);
        } catch (Exception e){
            message = e.getMessage();
        }

        assertEquals("Cannot merge unconnected Ways", message);
    }

    @Test
    public void MergeTestNullToWay(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        test = Way.merge(null, w1);

        assertEquals(w1.first(), test.first());
        assertEquals(w1.last(), test.last());
    }

    @Test
    public void MergeTestWayToNull(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1.getAsLong());
        w1.addNode(nd2.getAsLong());
        test = Way.merge(w1, null);

        assertEquals(w1.first(), test.first());
        assertEquals(w1.last(), test.last());
    }

    @Test
    public void removeNodeTest(){
        Way test = new Way();
        test.addNode(nd1.getAsLong());
        test.addNode(nd2.getAsLong());
        test.addNode(nd3.getAsLong());
        test.remove(nd2.getAsLong());
        assertEquals(test.getSize(), 2);
    }

    @Test
    public void removeNullTest(){
        Way test = new Way();
        test.addNode(nd1.getAsLong());
        test.remove(nd2.getAsLong());
        assertEquals(test.getSize(), 1);
    }

    @Test
    public void addNodeToFrontTest(){
        Way test = new Way();
        test.addNode(nd2.getAsLong());
        test.addNodeToFront(nd1.getAsLong());
        assertEquals(test.first(), 1);
    }

    @Test
    public void AddToFrontExtendTest(){
        Way test = new Way();
        for(int i = 0; i < 12; i++){
            test.addNodeToFront(nd1.getAsLong());
        }
    }
}
