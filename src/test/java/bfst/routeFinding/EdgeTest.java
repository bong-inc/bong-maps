package bfst.routeFinding;

import bfst.OSMReader.Node;
import bfst.canvas.Range;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {

    @Test
    void testGetCentroid() {
        Edge edge = new Edge(new Node(1, 1, 1), new Node(2, 3, 3), null);
        Point2D expected = new Point2D(2, 2);
        Point2D actual = edge.getCentroid();
        Assertions.assertEquals(expected.getX(), actual.getX());
        Assertions.assertEquals(expected.getY(), actual.getY());
    }

    @Test
    void testGetBoundingBox() {

        Edge edge = new Edge(new Node(1, 1, 2), new Node(2, 3, 4), null);
        Range expected = new Range(1, 2, 3, 4);
        Range actual = edge.getBoundingBox();

        Assertions.assertEquals(expected.getMinX(), actual.getMinX());
        Assertions.assertEquals(expected.getMinY(), actual.getMinY());
        Assertions.assertEquals(expected.getMaxX(), actual.getMaxX());
        Assertions.assertEquals(expected.getMaxY(), actual.getMaxY());

        edge = new Edge(new Node(1, 5, 4), new Node(2, 3, 2), null);
        expected = new Range(3, 2, 5, 4);
        actual = edge.getBoundingBox();

        Assertions.assertEquals(expected.getMinX(), actual.getMinX());
        Assertions.assertEquals(expected.getMinY(), actual.getMinY());
        Assertions.assertEquals(expected.getMaxX(), actual.getMaxX());
        Assertions.assertEquals(expected.getMaxY(), actual.getMaxY());
    }

}