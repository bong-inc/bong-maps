package bfst.canvas;

import bfst.canvas.Drawable;
import bfst.canvas.Range;
import javafx.geometry.Point2D;

public abstract class CanvasElement implements Drawable {
  public abstract Point2D getCentroid();
  public abstract Range getBoundingBox();
}