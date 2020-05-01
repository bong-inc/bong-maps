package bfst.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import bfst.canvas.Range;
import bfst.routeFinding.Edge;
import bfst.routeFinding.Street;
import bfst.util.Geometry;
import javafx.geometry.Point2D;

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
      List<Edge> lower = new ArrayList<>(this.elements.subList(0, (size) / 2 + 1));
      List<Edge> higher = new ArrayList<>(this.elements.subList((size) / 2 + 1, size));

      // set dimentions of new subtree (low)
      this.low = new KDTreeForEdges(lower, boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      // set dimentions of new subtree (high)
      this.high = new KDTreeForEdges(higher, boundingRangeOf(higher), this.depth + 1, Type.LEAF);
      
      // set this to parent node
      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  // Only used for road edges
  public Node nearestNeighbor(Point2D query, String vehicle){
    Node returnElement = nearestNeighbor(query, Double.POSITIVE_INFINITY, vehicle);
    try {
      if(returnElement == null) throw new Exception("No nearest neighbor found");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return returnElement;
  }

  private Node nearestNeighbor(Point2D query, double bestDist, String vehicle) {
    KDTreeForEdges first, last;
    if(!isLeaf()){
      Node result = null;

      first = low.bound.distanceToPoint(query) < high.bound.distanceToPoint(query) ? low : high;
      last = low.bound.distanceToPoint(query) > high.bound.distanceToPoint(query) ? low : high;

      if(first.bound.distanceToPoint(query) < bestDist){
        result = first.nearestNeighbor(query, bestDist, vehicle);
        if(result != null) bestDist = Geometry.distance(query, result.getCentroid());
      }
      Node temp;
      if(last.bound.distanceToPoint(query) < bestDist){
        temp = last.nearestNeighbor(query, bestDist, vehicle);
        if(temp != null){
          result = temp;
        }
      }
      return result;
    }

    if(isLeaf() && elements.size() > 0){
      Node result = null;
      Node c = closestNodeInElements(query, vehicle);
      if(c == null) return null;

      if(Geometry.distance(query, c.getCentroid()) < bestDist){
        result = c;
        bestDist = Geometry.distance(query, c.getCentroid());
      }
      return result;
    }
    return null;
  }

  private Range boundingRangeOf(List<Edge> list){
    if(list.size() < 1) return null;
    Float minX = Float.MAX_VALUE;
    Float minY = Float.MAX_VALUE;
    Float maxX = Float.NEGATIVE_INFINITY;
    Float maxY = Float.NEGATIVE_INFINITY;
    for(Edge c : list){
      Range boundingBox = c.getBoundingBox();
      if(boundingBox.getMinX() < minX) minX = boundingBox.getMinX();
      if(boundingBox.getMaxY() < minY) minY = boundingBox.getMinY();
      if(boundingBox.getMaxX() > maxX) maxX = boundingBox.getMaxX();
      if(boundingBox.getMaxY() > maxY) maxY = boundingBox.getMaxY();
    }
    return new Range(minX, minY, maxX, maxY);
  }

  private boolean isEvenDepth() {
    return this.depth % 2 == 0;
  }

  private boolean isLeaf() {
    return this.type == Type.LEAF;
  }

  public Node closestNodeInElements(Point2D query, String vehicle) {
    Edge closestEdge = null;
    Node closestNode = null;
    double bestDist = Double.POSITIVE_INFINITY;
    for(Edge e : elements) {

      Street street = e.getStreet();
      if(vehicle.equals("Car") && !street.isCar()) continue;
      if(vehicle.equals("Bicycle") && !street.isBicycle()) continue;
      if(vehicle.equals("Walk") && !street.isWalking()) continue;
      Node newNode = closestNodeInEdge(query, e);
      
      double newDist = Geometry.distance(query.getX(), query.getY(), newNode.getLon(), newNode.getLat());
      if(newDist < bestDist){
        closestEdge = e;
        bestDist = newDist;
        closestNode = newNode;
      }
    }
    return closestNode;
  }

  public Node closestNodeInEdge(Point2D query, Edge edge) {
    double distToTail = Geometry.distance(query.getX(), query.getY(), edge.getTailNode().getLon(), edge.getTailNode().getLat());
    double distToHead = Geometry.distance(query.getX(), query.getY(), edge.getHeadNode().getLon(), edge.getHeadNode().getLat());
    return distToTail < distToHead ? edge.getTailNode() : edge.getHeadNode();
  }

}