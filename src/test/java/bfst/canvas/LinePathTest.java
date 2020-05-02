package bfst.canvas;

import bfst.OSMReader.Node;
import bfst.OSMReader.NodeContainer;
import bfst.OSMReader.Way;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

    
class MockedDrawer extends Drawer {
    int lineToCount = 0;
    int moveToCount = 0;

    @Override 
    public void lineTo(double x, double y) {
        lineToCount++;
    }

    @Override 
    public void moveTo(double x, double y) {
        moveToCount++;
    }


    /**
     * @return the lineToCount
     */
    public int getLineToCount() {
        return lineToCount;
    }
}

public class LinePathTest {

    @Test
    void smartTraceTest() {

        MockedDrawer mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
            0,0 , 1,0
        }).smartTrace(mockDrawer, 1);
        assertEquals(1, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
            0,0 , 1,0 , 2,0
        }).smartTrace(mockDrawer, 1);
        assertEquals(1, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
            0,0 , 5,0 , 10,0
        }).smartTrace(mockDrawer, 1);
        assertEquals(2, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
            0,0 , 5,0 , 10,0
        }).smartTrace(mockDrawer, 1.5);
        assertEquals(2, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
            0,0 , 5,0 , 10,0
        }).smartTrace(mockDrawer, 0.5);
        assertEquals(1, mockDrawer.getLineToCount());
    }

    @Test
    void traceTest() {

        MockedDrawer mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
                0,0 , 1,0
        }).trace(mockDrawer);
        assertEquals(1, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
                0,0 , 1,0 , 2,0
        }).trace(mockDrawer);
        assertEquals(2, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
                0,0 , 5,0 , 10,0
        }).trace(mockDrawer);
        assertEquals(2, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
                0,0 , 5,0 , 10,0
        }).trace(mockDrawer);
        assertEquals(2, mockDrawer.getLineToCount());

        mockDrawer = new MockedDrawer();
        new LinePath(new float[]{
                0,0 , 5,0 , 10,0
        }).trace(mockDrawer);
        assertEquals(2, mockDrawer.getLineToCount());
    }

    @Test
    void testGetCoordsFormNodeContainer() {
        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 5,10);
        ndc.add(1, 6,20);
        ndc.add(2, 7,30);

        Way way = new Way();
        way.addNode(0);
        way.addNode(1);
        way.addNode(2);

        float[] expected = {5,10,6,20,7,30};
        float[] actual = LinePath.getCoordsFromNodeContainer(way, ndc);

        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void testLinePath() {

        float[] floats = {1,2,3,4};

        NodeContainer ndc = new NodeContainer();
        ndc.add(0, 1,2);
        ndc.add(1, 3,4);

        Way way = new Way();
        way.addNode(0);
        way.addNode(1);

        LinePath linePath1 = new LinePath(floats);
        LinePath linePath2 = new LinePath(way, ndc);
        LinePath linePath3 = new LinePath(new Node(0,1,2), new Node(1,3,4));

        for (int i = 0; i < 4; i++) {
            boolean actual = (linePath1.getCoords()[i] == linePath2.getCoords()[i]) == (linePath2.getCoords()[i] == linePath3.getCoords()[i]);
            Assertions.assertEquals(true, actual);
        }
    }

    @Test
    void testCalculateBoundingBox() {

        float[] floats = {1,1,3,4,5,0};
        LinePath linePath = new LinePath(floats);
        Range actual = linePath.calculateBoundingBox();

        Assertions.assertEquals(1, actual.getMinX());
        Assertions.assertEquals(5, actual.getMaxX());
        Assertions.assertEquals(4, actual.getMaxY());
        Assertions.assertEquals(0, actual.getMinY());
    }

}