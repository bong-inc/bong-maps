package bfst.canvas;

import java.io.Serializable;
import java.util.Arrays;

import bfst.util.Geometry;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Range implements Serializable {
  public float minX, minY, maxX, maxY;

  public Range(float minX, float minY, float maxX, float maxY) {
    this.minX = minX;
    this.minY = minY;
    this.maxX = maxX;
    this.maxY = maxY;
  }

  public void draw(GraphicsContext gc, double invertedZoomFactor){
    gc.setStroke(Color.BLUE);
    gc.setLineWidth(invertedZoomFactor);
    gc.strokeRect(minX, minY, maxX-minX, maxY-minY);
    gc.stroke();
  }

  public boolean isEnclosedBy(Range that){
    return this.minX < that.minX && this.maxX > that.maxX && this.minY < that.minY && this.maxY > that.maxY;
  }

  public boolean overlapsWith(Range that) {
    return !(that.minX > this.maxX || this.minX > that.maxX || that.minY > this.maxY || this.minY > that.maxY);
  }

  public double distanceToPoint(Point2D point){
    if(Geometry.pointInsideRange(point, this)){
      return 0.0;
    }
    double[] rangeDists = new double[]{
      Geometry.distanceToLineSegment(point, new Point2D(this.minX, this.minY), new Point2D(this.maxX, this.minY)),
      Geometry.distanceToLineSegment(point, new Point2D(this.maxX, this.minY), new Point2D(this.maxX, this.maxY)),
      Geometry.distanceToLineSegment(point, new Point2D(this.maxX, this.maxY), new Point2D(this.minX, this.maxY)),
      Geometry.distanceToLineSegment(point, new Point2D(this.minX, this.maxY), new Point2D(this.minX, this.minY)),
    };
    Arrays.sort(rangeDists);
    return rangeDists[0];
  }

  public Point2D getCentroid(){
    return new Point2D((this.maxX+this.minX)/2, (this.maxY+this.minY)/2);
  }
  
}