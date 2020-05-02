package bfst.canvas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScaleBarTest {

    @Test
    void testUpdateScaleBar() {

        MapCanvas canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.9, 0.9);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        String actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("50m", actual);
        Assertions.assertEquals(50, canvas.getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.4, 0.4);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("100m", actual);
        Assertions.assertEquals(100, canvas.getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.2, 0.2);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("250m", actual);
        Assertions.assertEquals(250, canvas.getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.1, 0.1);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("500m", actual);
        Assertions.assertEquals(500, canvas.getScaleBar().getBarLength(), 1);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.05, 0.05);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("1km", actual);
        Assertions.assertEquals(1000, canvas.getScaleBar().getBarLength(), 2);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.025, 0.025);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("2km", actual);
        Assertions.assertEquals(2000, canvas.getScaleBar().getBarLength(), 5);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.012, 0.012);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("5km", actual);
        Assertions.assertEquals(5000, canvas.getScaleBar().getBarLength(), 10);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.005, 0.005);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("10km", actual);
        Assertions.assertEquals(10000, canvas.getScaleBar().getBarLength(), 20);

        canvas = new MapCanvas();
        canvas.getTrans().prependScale(0.0025, 0.0025);
        canvas.repaint(50);
        canvas.getScaleBar().updateScaleBar(canvas);
        actual = canvas.getScaleBar().getBarShowing();
        Assertions.assertEquals("20km", actual);
        Assertions.assertEquals(20000, canvas.getScaleBar().getBarLength(), 40);
    }

}