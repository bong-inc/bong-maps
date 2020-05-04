package bong.OSMReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import bong.canvas.Drawer;
import bong.canvas.MapCanvas;

/**
 * BoundTest
 */
public class BoundTest {

    @Test
    public void boundTest() {
        Bound bound = new Bound(0f,0f,1f,1f);
        assertEquals(0f, bound.getMinLat());
        assertEquals(0f, bound.getMaxLat());
        assertEquals(1f, bound.getMinLon());
        assertEquals(1f, bound.getMaxLon());
        MapCanvas canvas = new MapCanvas();
        bound.draw(new Drawer(canvas.getGraphicsContext2D()), 1d, true);
    }
}