package bfst.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import bfst.canvas.Range;
import bfst.util.Geometry;
import bfst.canvas.CanvasElement;
import javafx.geometry.Point2D;

public class KDTree implements Serializable {
  private static final long serialVersionUID = 8179750180455602356L;
  List<CanvasElement> elements;
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
  public KDTree(List<CanvasElement> elements, Range bound){
    this(elements,bound,0,Type.LEAF);
  }

  /** Recursive constructor for intermediate nodes */
  public KDTree(List<CanvasElement> elements, Range bound, int depth, Type type){
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
      this.low = new KDTree(lower, boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      // set dimentions of new subtree (high)
      this.high = new KDTree(higher, boundingRangeOf(higher), this.depth + 1, Type.LEAF);
      
      // set this to parent node
      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  // Only used for Address objects
  public CanvasElement nearestNeighbor(Point2D query){
    CanvasElement returnElement = nearestNeighbor(query, Double.POSITIVE_INFINITY);
    try {
      if(returnElement == null) throw new Exception("No nearest neighbor found");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return returnElement;
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

  public static Range boundingRangeOf(List<CanvasElement> list){
    if(list.size() < 1) throw new RuntimeException("Empty list cannot have bounding range");
    Float minX = Float.MAX_VALUE;
    Float minY = Float.MAX_VALUE;
    Float maxX = Float.NEGATIVE_INFINITY;
    Float maxY = Float.NEGATIVE_INFINITY;
    for(CanvasElement c : list){
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

  public List<CanvasElement> rangeSearch(Range range){
    boolean isEnclosed = range.isEnclosedBy(bound);
    if(!range.overlapsWith(bound)) return new ArrayList<CanvasElement>();
    if(this.isLeaf()){
      List<CanvasElement> elementsInRange = new ArrayList<CanvasElement>();
      for(CanvasElement element : elements){
        Range boundingBox = element.getBoundingBox();
        if(isEnclosed || range.overlapsWith(boundingBox)){
          elementsInRange.add(element);
        }
      }

      return elementsInRange;
    } else {
      List<CanvasElement> elementsInLowRange = new ArrayList<CanvasElement>();
      List<CanvasElement> elementsInHighRange = new ArrayList<CanvasElement>();
      if(low != null) elementsInLowRange = low.rangeSearch(range);
      if(high != null) elementsInHighRange = high.rangeSearch(range);
      List<CanvasElement> newList = Stream.concat(elementsInLowRange.stream(), elementsInHighRange.stream()).collect(Collectors.toList());
      return newList;
    }
  }

}