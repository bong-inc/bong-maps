package bfst.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;

public class Drawer {
    GraphicsContext gc;

    public Drawer(GraphicsContext _gc) {
        gc = _gc;
	}
	
    public Drawer() {
    }

	public void setFill(Color color) {
		gc.setFill(color);
	}

	public void fillText(String text, double d, double e) {
		gc.fillText(text, d, e);
	}

	public void setTransform(Affine affine) {
		gc.setTransform(affine);
	}

	public void setFill(Paint color) {
		gc.setFill(color);
	}

	public void fillRect(int i, int j, double width, double height) {
		gc.fillRect(i, j, width, height);
	}

	public void setFillRule(FillRule fillRule) {
		gc.setFillRule(fillRule);
	}

	public void setStroke(Color valueOf) {
		gc.setStroke(valueOf);
	}

	public void setLineWidth(double d) {
		gc.setLineWidth(d);
	}

	public void beginPath() {
		gc.beginPath();
	}

	public void stroke() {
		gc.stroke();
	}

	public void moveTo(double placementX, double d) {
		gc.moveTo(placementX, d);
	}

	public void lineTo(double placementX, double placementY) {
		gc.lineTo(placementX, placementY);
	}

	public void appendSVGPath(String translated) {
		gc.appendSVGPath(translated);
	}

	public void closePath() {
		gc.closePath();
	}

	public void fill() {
		gc.fill();
	}

	public void strokeText(String name, double e, double d) {
		gc.strokeText(name, e, d);
	}

	public void setFont(Font font) {
		gc.setFont(font);
	}

	public void strokeOval(double d, double e, double radius, double radius2) {
		gc.strokeOval(d, e, radius, radius2);
	}

	public void fillOval(double d, double e, double radius, double radius2) {
		gc.fillOval(d, e, radius, radius2);
	}

	public void setTextAlign(TextAlignment center) {
		gc.setTextAlign(center);
	}

	public Paint getStroke() {
		return getStroke();
	}

	public void setStroke(Paint prevStroke) {
		gc.setStroke(prevStroke);
	}

	public void strokeRect(float minX, float minY, float f, float g) {
		gc.strokeRect(minX, minY, f, g);
	}


}