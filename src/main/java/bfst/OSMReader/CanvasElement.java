package bfst.OSMReader;

import bfst.canvas.Drawable;
import javafx.geometry.Point2D;

public abstract class CanvasElement implements Drawable {
  public abstract Point2D getCentroid();
}