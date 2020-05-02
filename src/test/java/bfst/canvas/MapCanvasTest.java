package bfst.canvas;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapCanvasTest {

    MapCanvas canvas = new MapCanvas();

    @Test
    void clearOriginDestinationTest() {
        canvas.setCurrentRouteDestination();
        canvas.setCurrentRouteOrigin();

        assertEquals(1, canvas.getCurrentRouteDestination().centerX);
        assertEquals(1, canvas.getCurrentRouteOrigin().centerX);

        canvas.clearOriginDestination();
        assertNull(canvas.getCurrentRouteDestination());
        assertNull(canvas.getCurrentRouteOrigin());
    }

    @Test
    void updateSearchRangeTest() {
        canvas = new MapCanvas();
        canvas.setRenderFullScreen(false);
        canvas.updateSearchRange(1);

        Range actual = canvas.getRenderRange();
        assertEquals(-100, actual.getMinX());
        assertEquals(-100, actual.getMinY());
        assertEquals(100, actual.getMaxX());
        assertEquals(100, actual.getMaxY());
    }

    @Test
    void shouldZoomTest() {
        Affine trans;
        boolean actual;

        canvas = new MapCanvas();
        trans = canvas.getTrans();
        trans.prependScale(1,1);
        actual = canvas.shouldZoom(1.5);
        assertTrue(actual);

        canvas = new MapCanvas();
        trans = canvas.getTrans();
        trans.prependScale(20,20);
        actual = canvas.shouldZoom(1.5);
        assertFalse(actual);

        canvas = new MapCanvas();
        trans = canvas.getTrans();
        trans.prependScale(1,1);
        actual = canvas.shouldZoom(0.5);
        assertTrue(actual);

        canvas = new MapCanvas();
        trans = canvas.getTrans();
        trans.prependScale(0.0001,0.0001);
        actual = canvas.shouldZoom(0.5);
        assertFalse(actual);
    }

}