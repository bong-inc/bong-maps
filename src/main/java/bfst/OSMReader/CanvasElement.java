package bfst.OSMReader;

import bfst.canvas.Drawable;
import bfst.canvas.Range;
import javafx.geometry.Point2D;

public abstract class CanvasElement implements Drawable {
  public abstract Point2D getCentroid();
  public abstract Range getBoundingBox();
  public abstract void setBoundingBox();

  public Point2D getCenterFromRange(Range range){
    return new Point2D(range.minX + (range.maxX-range.minX)/2, range.minY + (range.maxY-range.minY)/2);
  }
}