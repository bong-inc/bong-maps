package bong.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bong.canvas.Range;
import javafx.geometry.Point2D;

public class GeometryTest {

    @Test
    public void testPointInsideRange(){
      assertTrue(Geometry.pointInsideRange(new Point2D(0.5, 0.5), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.1, 0.1), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 0.0), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 1.0), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(1.0, 0.0), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(1.0, 1.0), new Range(0, 0, 1, 1)));
      
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 0.5), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.5, 0.0), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(1.0, 0.5), new Range(0, 0, 1, 1)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.5, 1.0), new Range(0, 0, 1, 1)));

      assertTrue(Geometry.pointInsideRange(new Point2D(0.5, 0.5), new Range(1, 1, 0, 0)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 0.0), new Range(0, 0, 0, 0)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 0.0), new Range(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)));
      assertTrue(Geometry.pointInsideRange(new Point2D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY), new Range(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 0.0), new Range(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.0, 0.0), new Range(0, 0, 1, 0)));
      assertTrue(Geometry.pointInsideRange(new Point2D(0.5, 0.5), new Range(0, 1, 1, 0)));

      assertFalse(Geometry.pointInsideRange(new Point2D(-0.1, 0.5), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(0.5, -0.1), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(1.1, 0.5), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(0.5, 1.1), new Range(0, 0, 1, 1)));

      assertFalse(Geometry.pointInsideRange(new Point2D(1.1, 1.1), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(1.1, -0.1), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(-0.1, 1.1), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(-0.1, -0.1), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(1.1, 0.5), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(0.5, 1.1), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(-0.1, 0.5), new Range(0, 0, 1, 1)));
      assertFalse(Geometry.pointInsideRange(new Point2D(0.5, -0.1), new Range(0, 0, 1, 1)));
    }

    @Test
    public void testDistanceToLineSegment(){
      assertEquals(0.0, Geometry.distanceToLineSegment(new Point2D(0, 0), new Point2D(0, 0), new Point2D(0, 0)));
      assertEquals(0.0, Geometry.distanceToLineSegment(new Point2D(5, 5), new Point2D(0, 0), new Point2D(10, 10)));
      assertEquals(0.0, Geometry.distanceToLineSegment(new Point2D(5, 0), new Point2D(0, 0), new Point2D(10, 0)));
      assertEquals(5.0, Geometry.distanceToLineSegment(new Point2D(0, 0), new Point2D(5, 0), new Point2D(10, 0)));
      assertEquals(5.0, Geometry.distanceToLineSegment(new Point2D(3, 4), new Point2D(-10, 0), new Point2D(0, 0)));
    }

    @Test
    public void testDistance(){
      assertEquals(0.0, Geometry.distance(new Point2D(0, 0), new Point2D(0, 0)));
      assertEquals(5.0, Geometry.distance(new Point2D(3, 4), new Point2D(0, 0)));
      assertEquals(0.001, Geometry.distance(new Point2D(0.001,0), new Point2D(0, 0)));
    }

    @Test
    public void testDistance2(){
      assertEquals(0.0, Geometry.distance(0,0,0,0));
      assertEquals(5.0, Geometry.distance(3,4,0,0));
      assertEquals(0.001, Geometry.distance(0.001,0,0,0));
    }

}