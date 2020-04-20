package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Range {
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
}