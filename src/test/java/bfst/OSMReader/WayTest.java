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

    @Test
    public void MergeTest(){
        Way test;
        Node nd1 = new Node(1, 1, 1);
        Node nd2 = new Node(2, 2, 2);
        Node nd3 = new Node(3, 3, 3);
        Node nd4 = new Node(4, 4, 4);

        //BASE
        Way w1 = new Way();
        w1.addNode(nd1);
        w1.addNode(nd2);

        //FIRST-FIRST
        Way w2 = new Way();
        w2.addNode(nd1);
        w2.addNode(nd3);

        test = Way.merge(w1, w2);
        assertEquals(w1.last(), test.first());
        assertEquals(w2.last(), test.last());

        //LAST-LAST
        Way w3 = new Way();
        w3.addNode(nd3);
        w3.addNode(nd2);

        test = Way.merge(w1, w3);
        assertEquals(w1.first(), test.first());
        assertEquals(w3.first(), test.last());

        //FIRST-LAST
        Way w4 = new Way();
        w4.addNode(nd3);
        w4.addNode(nd1);

        test = Way.merge(w1, w4);
        assertEquals(w4.first(), test.first());
        assertEquals(w1.last(), test.last());

        //LAST-FIRST
        Way w5 = new Way();
        w5.addNode(nd2);
        w5.addNode(nd3);

        test = Way.merge(w1, w5);
        assertEquals(w5.last(), test.last());
        assertEquals(w1.first(), test.first());

        //EXCEPTION
        Way w6 = new Way();
        w6.addNode(nd3);
        w6.addNode(nd4);

        try {
            test = Way.merge(w1, w6);
        } catch (Exception e){
            String s = e.getMessage();
        }

        //NULL
        test = Way.merge(w1, null);
        test = Way.merge(null, w1);
    }
}
