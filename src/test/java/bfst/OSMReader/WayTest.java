package bfst.OSMReader;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WayTest {

    @Test
    public void WayTest(){
        Node nd1 = new Node(1, 10, 10);
        Node nd2 = new Node(2, 20, 20);
        Node nd3 = new Node(3, 99, 22);

        Way way1 = new Way(1);
        assertEquals(1, way1.getAsLong());

        way1.addNode(nd2);
        assertEquals(nd2, way1.first());
        way1.addNodeToFront(nd1);
        assertEquals(nd1, way1.first());
        way1.addNode(nd3);
        assertEquals(nd3, way1.last());

        assertEquals(3, way1.getNodes().size());
    }

    Node nd1 = new Node(1, 1, 1);
    Node nd2 = new Node(2, 2, 2);
    Node nd3 = new Node(3, 3, 3);
    Node nd4 = new Node(4, 4, 4);

    @Test
    public void MergeTestFirstToFirst(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        Way w2 = new Way();
        w2.addNode(nd1);
        w2.addNode(nd3);

        test = Way.merge(w1, w2);
        assertEquals(w1.last(), test.first());
        assertEquals(w2.last(), test.last());
    }

    @Test
    public void MergeTestLastToLast(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        Way w2 = new Way();
        w2.addNode(nd3);
        w2.addNode(nd2);

        test = Way.merge(w1, w2);
        assertEquals(w1.first(), test.first());
        assertEquals(w2.first(), test.last());
    }

    @Test
    public void MergeTestFirstToLast(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        Way w2 = new Way();
        w2.addNode(nd3);
        w2.addNode(nd1);

        test = Way.merge(w1, w2);
        assertEquals(w2.first(), test.first());
        assertEquals(w1.last(), test.last());
    }

    @Test
    public void MergeTestLastToFirst(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        Way w2 = new Way();
        w2.addNode(nd2);
        w2.addNode(nd3);

        test = Way.merge(w1, w2);
        assertEquals(w2.last(), test.last());
        assertEquals(w1.first(), test.first());
    }

    @Test
    public void MergeTestUnconnected(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        Way w2 = new Way();
        w2.addNode(nd3);
        w2.addNode(nd4);

        String message = "";
        try {
            test = Way.merge(w1, w2);
        } catch (Exception e){
            message = e.getMessage();
        }

        assertEquals("Cannot merge unconnected OSMWays", message);
    }

    @Test
    public void MergeTestNullToWay(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        test = Way.merge(null, w1);

        assertEquals(w1.first(), test.first());
        assertEquals(w1.last(), test.last());
    }

    @Test
    public void MergeTestWayToNull(){
        Way test;
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);
        test = Way.merge(w1, null);

        assertEquals(w1.first(), test.first());
        assertEquals(w1.last(), test.last());
    }
}
