package bfst.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bfst.canvas.Range;
import bfst.canvas.CanvasElement;
import bfst.routeFinding.Edge;
import bfst.routeFinding.Street;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class KDTreeForEdges implements Serializable {
  private static final long serialVersionUID = 8179750180455602356L;
  List<Edge> elements;
  int maxNumOfElements = 500; // max size of elements
  Range bound;
  KDTreeForEdges low;
  KDTreeForEdges high;
  Type type;
  int depth;
  public static boolean drawBoundingBox;

  private enum Type {
    PARENT, LEAF
  }

  /** first instance is root and depth 0 */
  public KDTreeForEdges(List<Edge> elements, Range bound){
    this(elements,bound,0,Type.LEAF);
  }

  /** Recursive constructor for intermediate nodes */
  public KDTreeForEdges(List<Edge> elements, Range bound, int depth, Type type){
    this.bound = bound;
    this.elements = elements;
    this.type = type;
    this.depth = depth;
    int size = this.elements.size();

    if(this.elements.size() >= maxNumOfElements){
      if(this.isEvenDepth()){
        // sort by x
        Collections.sort(this.elements, new Comparator<Edge>() {
          @Override
          public int compare(Edge o1, Edge o2) {
            if(o1.getCentroid().getX() > o2.getCentroid().getX()) return 1;
            if(o1.getCentroid().getX() < o2.getCentroid().getX()) return -1;
            return 0;
          }
        });
      } else {
        Collections.sort(this.elements, new Comparator<Edge>() {
          @Override
          public int compare(Edge o1, Edge o2) {
            if(o1.getCentroid().getY() > o2.getCentroid().getY()) return 1;
            if(o1.getCentroid().getY() < o2.getCentroid().getY()) return -1;
            return 0;
          }
        });
      }
      // Split elements in half
      ArrayList<Edge> lower = new ArrayList<>(this.elements.subList(0, (size) / 2 + 1));
      ArrayList<Edge> higher = new ArrayList<>(this.elements.subList((size) / 2 + 1, size));

      // set dimentions of new subtree (low)
      this.low = new KDTreeForEdges(lower, boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      // set dimentions of new subtree (high)
      this.high = new KDTreeForEdges(higher, boundingRangeOf(higher), this.depth + 1, Type.LEAF);
      
      // set this to parent node
      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  private Range boundingRangeOf(ArrayList<Edge> list){
    if(list.size() < 1) throw new RuntimeException("Empty list cannot have bounding range");
    Float minX = Float.MAX_VALUE;
    Float minY = Float.MAX_VALUE;
    Float maxX = Float.NEGATIVE_INFINITY;
    Float maxY = Float.NEGATIVE_INFINITY;
    for(Edge c : list){
      Range boundingBox = c.getBoundingBox();
      if(boundingBox.minX < minX) minX = boundingBox.minX;
      if(boundingBox.minY < minY) minY = boundingBox.minY;
      if(boundingBox.maxX > maxX) maxX = boundingBox.maxX;
      if(boundingBox.maxY > maxY) maxY = boundingBox.maxY;
    }
    return new Range(minX, minY, maxX, maxY);
  }

  private boolean isEvenDepth() {
    return this.depth % 2 == 0;
  }

  private boolean isLeaf() {
    return this.type == Type.LEAF;
  }

  private boolean isEnclosed(Range r1, Range r2) {
    return r1.minX < r2.minX && r1.maxX > r2.maxX && r1.minY < r2.minY && r1.maxY > r2.maxY;
  }

  public boolean doOverlap(Range r1, Range r2){
    return !(r2.minX > r1.maxX || r1.minX > r2.maxX || r2.minY > r1.maxY || r1.minY > r2.maxY);
  }

  public double distance(Point2D point1, Point2D point2){
    return distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
  }

  public double distance(double x1, double y1, double x2, double y2){
    return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
  }

  public double distanceToBox(Point2D query){
    if(pointInsideRange(query,bound)){
      return 0.0;
    }
    double[] boundDists = new double[]{
      distToSegment(query, new Point2D(bound.minX, bound.minY), new Point2D(bound.maxX, bound.minY)),
      distToSegment(query, new Point2D(bound.maxX, bound.minY), new Point2D(bound.maxX, bound.maxY)),
      distToSegment(query, new Point2D(bound.maxX, bound.maxY), new Point2D(bound.minX, bound.maxY)),
      distToSegment(query, new Point2D(bound.minX, bound.maxY), new Point2D(bound.minX, bound.minY)),
    };
    Arrays.sort(boundDists);
    return boundDists[0];
  }

  double distToSegment(Point2D query, Point2D start, Point2D end){
    double dx = end.getX() - start.getX();
    double dy = end.getY() - start.getY();
    double l2 = dx * dx + dy * dy;
    
    if (l2 == 0)
        return this.dist(query, start);

    var t = ((query.getX() - start.getX()) * dx + (query.getY() - start.getY()) * dy) / l2;
    t = Math.max(0, Math.min(1, t));

    return this.dist(query, new Point2D(start.getX() + t * dx, start.getY() + t * dy));
  }

  double dist(Point2D p1, Point2D p2) {
    var dx = p2.getX() - p1.getX();
    var dy = p2.getY() - p1.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }

  private boolean pointInsideRange(Point2D point, Range range) {
    return range.minX < point.getX() && point.getX() < range.maxX 
    && range.minY < point.getY() && point.getY() < range.maxY;
  }

  public Node bestInElements(Point2D query, String vehicle) {
    Edge bestEdge = null;
    Node bestNode = null;
    double bestDist = Double.POSITIVE_INFINITY;
    for(Edge e : elements) {
      Street s = e.getStreet();
      if(!(vehicle.equals("Car") && s.isCar())) continue;
      if(!(vehicle.equals("Bicycle") && s.isBicycle())) continue;
      if(!(vehicle.equals("Walk") && s.isWalking())) continue;

      Node newNode = bestNodeInEdge(query, e);
      double newDist = distance(query.getX(), query.getY(), newNode.getLon(), newNode.getLat());
      if(newDist < bestDist){
        bestEdge = e;
        bestDist = newDist;
        bestNode = newNode;
      }
    }
    return bestNode;
  }

  public Node bestNodeInEdge(Point2D query, Edge edge) {
    double distToTail = distance(query.getX(), query.getY(), edge.getTailNode().getLon(), edge.getTailNode().getLat());
    double distToHead = distance(query.getX(), query.getY(), edge.getHeadNode().getLon(), edge.getHeadNode().getLat());
    return distToTail < distToHead ? edge.getTailNode() : edge.getHeadNode();
  }

  public Node nearestNeighbor(Point2D query, double bestDist, String vehicle) {
    KDTreeForEdges first, last;
    if(!isLeaf()){
      Node result = null;

      first = low.distanceToBox(query) < high.distanceToBox(query) ? low : high;
      last = low.distanceToBox(query) > high.distanceToBox(query) ? low : high;

      if(first.distanceToBox(query) < bestDist){
        result = first.nearestNeighbor(query, bestDist, vehicle);
        if(result != null) bestDist = distance(query, result.getCentroid());
      }
      Node temp;
      if(last.distanceToBox(query) < bestDist){
        temp = last.nearestNeighbor(query, bestDist, vehicle);
        if(temp != null){
          result = temp;
        }
      }
      return result;
    }


    if(isLeaf() && elements.size() > 0){
      Node result = null;
      Node c = bestInElements(query, vehicle);
      if(c == null) return null;

      if(distance(query, c.getCentroid()) < bestDist){
        result = c;
        bestDist = distance(query, c.getCentroid());
      }

      return result;
    }

    return null;
  }

}