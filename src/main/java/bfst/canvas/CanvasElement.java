package bfst.canvas;

import javafx.geometry.Point2D;

public abstract class CanvasElement implements Drawable {
  public abstract Point2D getCentroid();
  public abstract Range getBoundingBox();
}