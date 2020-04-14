package bfst.OSMReader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeContainerTest {

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
