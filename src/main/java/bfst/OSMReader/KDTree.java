package bfst.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bfst.canvas.Range;
import bfst.canvas.CanvasElement;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class KDTree implements Serializable {
  private static final long serialVersionUID = 8179750180455602356L;
  List<CanvasElement> elements;
  public static int maxNumOfElements = 500; // max size of elements list in leafs
  Range bound;
  KDTree low;
  KDTree high;
  Type type;
  int depth;
  public static boolean drawBoundingBox;

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
      ArrayList<CanvasElement> lower = new ArrayList<>(this.elements.subList(0, (size) / 2 + 1));
      ArrayList<CanvasElement> higher = new ArrayList<>(this.elements.subList((size) / 2 + 1, size));

      // set dimentions of new subtree (low)
      this.low = new KDTree(lower, boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      // set dimentions of new subtree (high)
      this.high = new KDTree(higher, boundingRangeOf(higher), this.depth + 1, Type.LEAF);
      
      // set this to parent node
      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  private Range boundingRangeOf(ArrayList<CanvasElement> list){
    if(list.size() < 1) throw new RuntimeException("Empty list cannot have bounding range");
    Float minX = Float.MAX_VALUE;
    Float minY = Float.MAX_VALUE;
    Float maxX = Float.NEGATIVE_INFINITY;
    Float maxY = Float.NEGATIVE_INFINITY;
    for(CanvasElement c : list){
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

  public void draw(GraphicsContext gc, double scale, boolean smartTrace, boolean shouldHaveFill, Range range){
    // print number of trees visited
    // System.out.print("draw");

    // draw CanvasElements in leafValues
    boolean isEnclosed = isEnclosed(range,bound);
    if(doOverlap(range,bound)){
      if(this.isLeaf()){
        for(CanvasElement c : elements){
          Range boundingBox = c.getBoundingBox();
          if(isEnclosed || doOverlap(range, boundingBox)){
            c.draw(gc, scale, smartTrace);
            if (shouldHaveFill) gc.fill();

            if(drawBoundingBox){
              Paint tempColor = gc.getStroke();
              gc.setStroke(Color.PINK);
              gc.strokeRect(boundingBox.minX, boundingBox.minY, boundingBox.maxX -boundingBox.minX, boundingBox.maxY -boundingBox.minY);
              gc.stroke();
              gc.setStroke(tempColor);
              gc.stroke();
            }
          }
        }
      } else {
        if(low != null) low.draw(gc, scale, smartTrace, shouldHaveFill, range);
        if(high != null) high.draw(gc, scale, smartTrace, shouldHaveFill, range);
      }
    }    
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
    var dx = end.getX() - start.getX();
    var dy = end.getY() - start.getY();
    var l2 = dx * dx + dy * dy;
    
    if (l2 == 0)
        return this.dist(query, start);

    var t = ((query.getX() - start.getX()) * dx + (query.getY() - start.getY()) * dy) / l2;
    t = Math.max(0, Math.min(1, t));

    return this.dist(query, new Point2D(start.getX() + t * dx, start.getY() + t * dy));
  }

  double dist(Point2D p1, Point2D p2){
    var dx = p2.getX() - p1.getX();
    var dy = p2.getY() - p1.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }

  private boolean pointInsideRange(Point2D point, Range range) {
    return range.minX < point.getX() && point.getX() < range.maxX 
    && range.minY < point.getY() && point.getY() < range.maxY;
  }

  public CanvasElement bestInElements(Point2D query){
    CanvasElement best = elements.get(0);
    double bestDist = distance(query, best.getCentroid());
    for(CanvasElement c : elements){
      double newDist = distance(query, c.getCentroid());
      if(newDist < bestDist){
        bestDist = newDist;
        best = c;
      }
    }
    return best;
  }

  public CanvasElement nearestNeighbor(Point2D query){
    return nearestNeighbor(query, Double.POSITIVE_INFINITY);
  }

  private CanvasElement nearestNeighbor(Point2D query, double bestDist) {
    KDTree first, last;
    if(!isLeaf()){
      CanvasElement result = null;

      first = low.distanceToBox(query) < high.distanceToBox(query) ? low : high;
      last = low.distanceToBox(query) > high.distanceToBox(query) ? low : high;

      if(first.distanceToBox(query) < bestDist){
        result = first.nearestNeighbor(query, bestDist);
        if(result != null) bestDist = distance(query, result.getCentroid());
      }
      CanvasElement temp;
      if(last.distanceToBox(query) < bestDist){
        temp = last.nearestNeighbor(query, bestDist);
        if(temp != null){
          result = temp;
        }
      }
      return result;
    }


    if(isLeaf() && elements.size() > 0){
      CanvasElement result = null;
      CanvasElement c = bestInElements(query);
      if(distance(query, c.getCentroid()) < bestDist){
        result = c;
        bestDist = distance(query, c.getCentroid());
      }

      return result;
    }

    return null;
  }

}