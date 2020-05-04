package bong.OSMReader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeContainerTest {

    @Test
    public void getTest(){
        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 5,5);
        ndc.add(1, 6,6);
        ndc.add(2, 7,7);

        Node gotten = ndc.get(1);
        assertEquals(1, gotten.getAsLong());
        assertEquals(6, gotten.getLon());
        assertEquals(6, gotten.getLat());
    }

    @Test
    public void getIndexTest(){
        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 0,0);
        ndc.add(2, 0,0);
        ndc.add(1, 0,0);

        assertEquals(1, ndc.getIndex(1));
    }

    @Test
    public void getLonFromIndex(){
        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 5,5);
        ndc.add(1, 6,6);
        ndc.add(2, 7,7);

        assertEquals(6, ndc.getLonFromIndex(1));
    }

    @Test
    public void getLatFromIndex(){
        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 5,5);
        ndc.add(1, 6,6);
        ndc.add(2, 7,7);

        assertEquals(6, ndc.getLatFromIndex(1));
    }

    @Test
    public void getSizeTest(){
        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 0,0);
        ndc.add(1, 0,0);
        ndc.add(2, 0,0);
        assertEquals(3, ndc.getSize());
    }

    @Test
    public void ExtendTest(){
        NodeContainer ndc = new NodeContainer();
        for(int i = 0; i < 12; i++){
            ndc.add(i, 0,0);
        }
    }

    @Test
    public void getNullTest(){
        NodeContainer ndc = new NodeContainer();
        assertNull(ndc.get(1));
    }

    @Test
    public void resortTest(){
        NodeContainer ndc = new NodeContainer();
        ndc.add(0,0,0);
        ndc.get(0);
        ndc.add(1,0,0);
    }
}
