package bfst.util;

import bfst.canvas.Range;
import javafx.geometry.Point2D;

public class Geometry {

  public static boolean pointInsideRange(Point2D point, Range range) {
    return range.minX <= point.getX() && point.getX() <= range.maxX 
    && range.minY <= point.getY() && point.getY() <= range.maxY;
  }

  public static double distanceToLineSegment(Point2D query, Point2D start, Point2D end){
    var dx = end.getX() - start.getX();
    var dy = end.getY() - start.getY();
    var l2 = dx * dx + dy * dy;
    
    if (l2 == 0)
        return distance(query, start);

    var t = ((query.getX() - start.getX()) * dx + (query.getY() - start.getY()) * dy) / l2;
    t = Math.max(0, Math.min(1, t));

    return distance(query, new Point2D(start.getX() + t * dx, start.getY() + t * dy));
  }

  public static double distance(Point2D point1, Point2D point2){
    return distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
  }

  public static double distance(double x1, double y1, double x2, double y2){
    return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
  }

}