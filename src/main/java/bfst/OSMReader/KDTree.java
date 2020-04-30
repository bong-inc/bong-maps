package bfst.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bfst.canvas.Range;
import bfst.util.Geometry;
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
      this.low = new KDTree(lower, Geometry.boundingRangeOf(lower), this.depth + 1, Type.LEAF);

      // set dimentions of new subtree (high)
      this.high = new KDTree(higher, Geometry.boundingRangeOf(higher), this.depth + 1, Type.LEAF);
      
      // set this to parent node
      this.elements = null;
      this.type = Type.PARENT;
    }
  }

  private boolean isEvenDepth() {
    return this.depth % 2 == 0;
  }

  public void draw(GraphicsContext gc, double scale, boolean smartTrace, boolean shouldHaveFill, Range range){
    // print number of trees visited
    // System.out.print("draw");

    // draw CanvasElements in leafValues
    boolean isEnclosed = range.isEnclosedBy(bound);
    if(range.overlapsWith(bound)){
      if(this.isLeaf()){
        for(CanvasElement c : elements){
          Range boundingBox = c.getBoundingBox();
          if(isEnclosed || range.overlapsWith(boundingBox)){
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

  public boolean isLeaf() {
    return this.type == Type.LEAF;
  }

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
      CanvasElement c = bestInElements(query);
      if(Geometry.distance(query, c.getCentroid()) < bestDist){
        result = c;
        bestDist = Geometry.distance(query, c.getCentroid());
      }

      return result;
    }

    return null;
  }

  public CanvasElement bestInElements(Point2D query){
    CanvasElement best = elements.get(0);
    double bestDist = Geometry.distance(query, best.getCentroid());
    for(CanvasElement c : elements){
      double newDist = Geometry.distance(query, c.getCentroid());
      if(newDist < bestDist){
        bestDist = newDist;
        best = c;
      }
    }
    return best;
  }

}