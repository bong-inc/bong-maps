package bong.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import bong.canvas.Range;
import bong.routeFinding.Edge;
import bong.routeFinding.Street;
import bong.util.Geometry;
import bong.canvas.CanvasElement;
import javafx.geometry.Point2D;

public class KDTree implements Serializable {
  private static final long serialVersionUID = 1L;
  List<? extends CanvasElement> elements;
  public static int maxNumOfElements = 500; // max size of elements list in leafs
  Range bound;
  KDTree low;
  KDTree high;
  Type type;
  int depth;

  private enum Type {
    PARENT, LEAF
  }

  /** first instance is root and depth 0 */
  public KDTree(List<? extends CanvasElement> elements, Range bound){
    this(elements,bound,0,Type.LEAF);
  }

  /** Recursive constructor for intermediate nodes */
  public KDTree(List<? extends CanvasElement> elements, Range bound, int depth, Type type){
    this.bound = bound;
    this.elements = elements;
    this.type = type;
    this.depth = depth;
    int size = this.elements.size();

    if(this.elements.size() >= maxNumOfElements){
      if(this.isEvenDepth()){
        // sort by x
        Collections.sort(this.elements, new Comparator<CanvasElement>() {
          @Override
          public int compare(CanvasElement o1, CanvasElement o2) {
            if(o1.getCentroid().getX() > o2.getCentroid().getX()) return 1;
            if(o1.getCentroid().getX() < o2.getCentroid().getX()) return -1;
            return 0;
          }
        });
      } else {
        Collections.sort(this.elements, new Comparator<CanvasElement>() {
          @Override
          public int compare(CanvasElement o1, CanvasElement o2) {
            if(o1.getCentroid().getY() > o2.getCentroid().getY()) return 1;
            if(o1.getCentroid().getY() < o2.getCentroid().getY()) return -1;
            return 0;
          }
        });
      }
      // Split elements in half
      List<CanvasElement> lower = new ArrayList<>(this.elements.subList(0, (size) / 2 + 1));
      List<CanvasElement> higher = new ArrayList<>(this.elements.subList((size) / 2 + 1, size));

      // set dimentions of new subtree (low)
      this.low = new KDTree(lower, CanvasElement.boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      // set dimentions of new subtree (high)
      this.high = new KDTree(higher, CanvasElement.boundingRangeOf(higher), this.depth + 1, Type.LEAF);
      
      // set this to parent node
      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  // Only used for Address objects
  public CanvasElement nearestNeighbor(Point2D query){
    return nearestNeighbor(query, Double.POSITIVE_INFINITY);
  }

  private CanvasElement nearestNeighbor(Point2D query, double bestDist) {
    KDTree first, last;
    if(!isLeaf()){
      CanvasElement result = null;

      first = low.bound.distanceToPoint(query) < high.bound.distanceToPoint(query) ? low : high;
      last = low.bound.distanceToPoint(query) > high.bound.distanceToPoint(query) ? low : high;

      if(first.bound.distanceToPoint(query) < bestDist){
        result = first.nearestNeighbor(query, bestDist);
        if(result != null) bestDist = Geometry.distance(query, result.getCentroid());
      }
      CanvasElement temp;
      if(last.bound.distanceToPoint(query) < bestDist){
        temp = last.nearestNeighbor(query, bestDist);
        if(temp != null){
          result = temp;
        }
      }
      return result;
    }

    if(isLeaf() && elements.size() > 0){
      CanvasElement result = null;
      CanvasElement c = closestElementInElements(query);
      if(c == null) return null;

      if(Geometry.distance(query, c.getCentroid()) < bestDist){
        result = c;
        bestDist = Geometry.distance(query, c.getCentroid());
      }
      return result;
    }
    return null;
  }

  private boolean isEvenDepth() {
    return this.depth % 2 == 0;
  }

  private boolean isLeaf() {
    return this.type == Type.LEAF;
  }

  public CanvasElement closestElementInElements(Point2D query){
    CanvasElement closestElement = elements.get(0);
    double bestDist = Geometry.distance(query, closestElement.getCentroid());
    for(CanvasElement element : elements){
      double newDist = Geometry.distance(query, element.getCentroid());
      if(newDist < bestDist){
        bestDist = newDist;
        closestElement = element;
      }
    }
    return closestElement;
  }

  public Stream<? extends CanvasElement> rangeSearch(Range range){
    if(!range.overlapsWith(bound)) return Stream.empty();
    if(this.isLeaf()){
      if(bound.isEnclosedBy(range)) return elements.stream();

      List<CanvasElement> elementsInRange = new ArrayList<CanvasElement>();
      for(CanvasElement element : elements){
        Range boundingBox = element.getBoundingBox();
        if(range.overlapsWith(boundingBox)){
          elementsInRange.add(element);
        }
      }

      return elementsInRange.stream();
    } else {
      Stream<? extends CanvasElement> elementsInLowRange;
      Stream<? extends CanvasElement> elementsInHighRange;
      Stream<? extends CanvasElement> newStream = null;
      if(low != null && high != null){
        elementsInLowRange = low.rangeSearch(range);
        elementsInHighRange = high.rangeSearch(range);
        newStream = Stream.concat(elementsInLowRange, elementsInHighRange);
      }
      return newStream;
    }
  }

  // Only used for road edges
  public Node nearestNeighborForEdges(Point2D query, String vehicle){
    return nearestNeighborForEdges(query, Double.POSITIVE_INFINITY, vehicle);
  }

  private Node nearestNeighborForEdges(Point2D query, double bestDist, String vehicle) {
    KDTree first, last;
    if(!isLeaf()){
      Node result = null;

      first = low.bound.distanceToPoint(query) < high.bound.distanceToPoint(query) ? low : high;
      last = low.bound.distanceToPoint(query) > high.bound.distanceToPoint(query) ? low : high;

      if(first.bound.distanceToPoint(query) < bestDist){
        result = first.nearestNeighborForEdges(query, bestDist, vehicle);
        if(result != null) bestDist = Geometry.distance(query, result.getCentroid());
      }
      Node temp;
      if(last.bound.distanceToPoint(query) < bestDist){
        temp = last.nearestNeighborForEdges(query, bestDist, vehicle);
        if(temp != null){
          result = temp;
        }
      }
      return result;
    }

    if(isLeaf() && elements.size() > 0){
      Node result = null;
      Node c = closestNodeInEdges(query, vehicle);
      if(c == null) return null;

      if(Geometry.distance(query, c.getCentroid()) < bestDist){
        result = c;
        bestDist = Geometry.distance(query, c.getCentroid());
      }
      return result;
    }
    return null;
  }

  public Node closestNodeInEdges(Point2D query, String vehicle) {
    Node closestNode = null;
    double bestDist = Double.POSITIVE_INFINITY;
    List<Edge> edges = (List<Edge>)(List<?>) elements;
    for(Edge e : edges) {

      Street street = e.getStreet();
      if(vehicle.equals("Car") && !street.isCar()) continue;
      if(vehicle.equals("Bicycle") && !street.isBicycle()) continue;
      if(vehicle.equals("Walk") && !street.isWalking()) continue;
      Node newNode = e.closestNode(query);
      
      double newDist = Geometry.distance(query.getX(), query.getY(), newNode.getLon(), newNode.getLat());
      if(newDist < bestDist){
        bestDist = newDist;
        closestNode = newNode;
      }
    }
    return closestNode;
  }

}