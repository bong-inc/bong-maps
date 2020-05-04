package bong.canvas;

import java.util.List;

import javafx.geometry.Point2D;

public abstract class CanvasElement implements Drawable {
  public abstract Point2D getCentroid();
  public abstract Range getBoundingBox();

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
}