package bfst.OSMReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bfst.canvas.Range;
import bfst.controllers.MainController;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class KDTree implements Serializable {
  ArrayList<CanvasElement> leafValues;
  int maxNumOfLeafValues = 100; // max size of leafValues
  double x, y, w, h;
  KDTree low;
  KDTree high;
  int depth;
  Type type;
  Double split;
  CanvasElement splitPoint;
  KDTree root;

  private enum Type {
    PARENT, LEAF
  }

  /** first instance is root and depth 0 */
  public KDTree(ArrayList<CanvasElement> leafValues, double x, double y, double w, double h){
    this(leafValues,x,y,w,h,0,Type.LEAF);
  }

  /** Recursive constructor for intermediate nodes */
  public KDTree(ArrayList<CanvasElement> leafValues, double x, double y, double w, double h, int depth, Type type){
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.depth = depth;
    this.leafValues = leafValues;
    this.type = type;
    int size = this.leafValues.size();

    if(this.leafValues.size() >= maxNumOfLeafValues){

      if(this.isEvenDepth()){

        // Split leafValues in half

        // sort by x
        Collections.sort(this.leafValues, new Comparator<CanvasElement>() {
          @Override
          public int compare(CanvasElement o1, CanvasElement o2) {
            if(o1.getCentroid().getX() > o2.getCentroid().getX()) return 1;
            if(o1.getCentroid().getX() < o2.getCentroid().getX()) return -1;
            return 0;
          }
        });
        ArrayList<CanvasElement> lower = new ArrayList<>(this.leafValues.subList(0, (size) / 2 + 1));
        ArrayList<CanvasElement> upper = new ArrayList<>(this.leafValues.subList((size) / 2 + 1, size));

        // set split value to last x value of lower array (lower half)
        splitPoint = (lower.size()>0) ? lower.get(lower.size()-1) : null;
        split = (splitPoint != null) ? splitPoint.getCentroid().getX() : null;

        // set dimentions of new subtree (low)
        double lowerX = this.x;
        double lowerY = this.y;
        double lowerW = lower.get(lower.size() - 1).getCentroid().getX() - this.x;
        double lowerH = this.h;
        this.low = new KDTree(lower, lowerX, lowerY, lowerW, lowerH, this.depth + 1, Type.LEAF);

        // set dimentions of new subtree (high)
        double upperX = lowerW + lowerX;
        double upperY = this.y;
        double upperW = this.w - lowerW;
        double upperH = this.h;
        this.high = new KDTree(upper, upperX, upperY, upperW, upperH, this.depth + 1, Type.LEAF);
        
        // set this to parent node
        this.leafValues = null;
        this.type = Type.PARENT;
      } else {

        // Split leafValues in half

        // sort by y
        Collections.sort(this.leafValues, new Comparator<CanvasElement>() {
          @Override
          public int compare(CanvasElement o1, CanvasElement o2) {
            Point2D o1Centroid = o1.getCentroid();
            Point2D o2Centroid = o2.getCentroid();
            if(o1Centroid.getY() > o2Centroid.getY()) return 1;
            if(o1Centroid.getY() < o2Centroid.getY()) return -1;
            return 0;
          }
        });
        ArrayList<CanvasElement> lower = new ArrayList<>(this.leafValues.subList(0, (size) / 2 + 1));
        ArrayList<CanvasElement> upper = new ArrayList<>(this.leafValues.subList((size) / 2 + 1, size));

        // set split value to last y value of lower array (lower half)
        splitPoint = (lower.size()>0) ? lower.get(lower.size()-1) : null;
        split = (splitPoint != null) ? splitPoint.getCentroid().getY() : null;

        // set dimentions of new subtree (low)
        double lowerX = this.x;
        double lowerY = this.y;
        double lowerW = this.w;
        double lowerH = lower.get(lower.size() - 1).getCentroid().getY() - this.y;
        this.low = new KDTree(lower, lowerX, lowerY, lowerW, lowerH, this.depth + 1, Type.LEAF);

        // set dimentions of new subtree (high)
        double upperX = this.x;
        double upperY = lowerH + lowerY;
        double upperW = this.w;
        double upperH = this.h - lowerH;
        this.high = new KDTree(upper, upperX, upperY, upperW, upperH, this.depth + 1, Type.LEAF);
        
        // set this to parent node
        this.leafValues = null;
        this.type = Type.PARENT;
      }
      
    }
  }

  public void draw(GraphicsContext gc, double scale, boolean smartTrace, boolean shouldHaveFill, Range range){
    // print number of trees visited
    // System.out.print("*");

    // draw CanvasElements in leafValues
    if(this.doOverlap(range)){
      if(this.isLeaf()){
        for(CanvasElement c : leafValues){
          c.draw(gc, scale, smartTrace);
          if (shouldHaveFill) gc.fill();
        }
      } else {
        if(low != null) low.draw(gc, scale, smartTrace, shouldHaveFill, range);
        if(high != null) high.draw(gc, scale, smartTrace, shouldHaveFill, range);
      }

      // draw outline/dimentions of all kdtrees
      // gc.setLineWidth(1/scale);
      // if(this.isEvenDepth()){
      //   gc.setStroke(Color.BLACK);
      // } else {
      //   gc.setStroke(Color.RED);
      // }
      // gc.strokeRect(x, y, w, h);
      // gc.stroke();
    }    
  }

  private boolean isSplitOnX() {
    return isEvenDepth();
  }

  private boolean isEvenDepth() {
    return this.depth % 2 == 0;
  }

  private boolean isLeaf() {
    return this.type == Type.LEAF;
  }

  public boolean doOverlap(Range range){
    double maxX = x+w;
    double maxY = y+h;

    if (maxY < range.minY || maxX < range.minX)
      return false;

    if (range.maxY < y || range.maxX < x) 
      return false;

    return true;
  }

  public double distance(Point2D point1, Point2D point2){
    return distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
  }

  public double distance(double x1, double y1, double x2, double y2){
    return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
  }

  public double distanceToBox(Point2D query){
    if(isSplitOnX()){
      return Math.abs(query.getX() - splitPoint.getCentroid().getX());
    } else {
      return Math.abs(query.getY() - splitPoint.getCentroid().getY());
    }
  }

  public boolean isLower(Point2D query){
    if(isSplitOnX()){
      return query.getX() < splitPoint.getCentroid().getX();
    } else {
      return query.getY() < splitPoint.getCentroid().getY();
    }
  }

  public CanvasElement bestInLeafValues(Point2D query){
    CanvasElement best = leafValues.get(0);
    double bestDist = distance(query, best.getCentroid());
    for(CanvasElement c : leafValues){
      double newDist = distance(query, c.getCentroid());
      if(newDist < bestDist){
        bestDist = newDist;
        best = c;
      }
    }
    return best;
  }

  public void nearestNeighbor(Point2D query) {
    if(!isLeaf()){
      if(MainController.champion == null) MainController.champion = splitPoint;
      double dist = distance(query, splitPoint.getCentroid());
      double currentDist = distance(query, MainController.champion.getCentroid());
      if(dist < currentDist){
        MainController.champion = splitPoint;
      }
    }

    if(!isLeaf()){ 
      boolean searchLowFirst = isLower(query);
      if(searchLowFirst){
        if(low != null) low.nearestNeighbor(query);
        if(distanceToBox(query) < distance(query, MainController.champion.getCentroid())) high.nearestNeighbor(query);
      } else {
        if(high != null) high.nearestNeighbor(query);
        if(distanceToBox(query) < distance(query, MainController.champion.getCentroid())) low.nearestNeighbor(query);
      }
    }

    if(isLeaf()){
      if(leafValues.size() > 0){
        if(MainController.champion2 == null){
          MainController.champion2 = bestInLeafValues(query);
        } else {
          CanvasElement bestInLeafValues = bestInLeafValues(query);
          if(distance(query, bestInLeafValues.getCentroid()) < distance(query, MainController.champion2.getCentroid())){
            MainController.champion2 = bestInLeafValues;
          }
        }
      }
    }
  }

}