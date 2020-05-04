package bong.OSMReader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MercatorProjectorTest {
    @Test
    public void positiveMercatorProjectorTest(){
        Node nd = MercatorProjector.project(12, 55);
        assertEquals(1335833.875, nd.getLon());
        assertEquals(7361866.0, nd.getLat());
    }

    @Test
    public void negativeMercatorProjectorTest(){
        Node nd = MercatorProjector.project(-12, -55);
        assertEquals(-1335833.875, nd.getLon());
        assertEquals(-7361866.0, nd.getLat());
    }

    @Test
    public void zeroMercatorProjectorTest(){
        Node nd = MercatorProjector.project(0, 0);
        assertEquals(0.0, nd.getLon());
        assertEquals(-7.081154551613622E-10, nd.getLat());
    }

    @Test
    public void extremeMercatorProjectorTest(){
        Node nd = MercatorProjector.project(360, 90);
        assertEquals(4.0075016E7, nd.getLon());
        assertEquals(2.38107696E8, nd.getLat());
    }

    @Test
    public void positiveMercatorUnprojectorTest(){
        Node nd = MercatorProjector.unproject(1335833.875, 7361866.0);
        assertEquals(12, nd.getLon());
        assertEquals(55, nd.getLat());
    }

    @Test
    public void negativeMercatorUnprojectorTest(){
        Node nd = MercatorProjector.unproject(-1335833.875, -7361866.0);
        assertEquals(-12, nd.getLon());
        assertEquals(-55, nd.getLat());
    }

    @Test
    public void zeroMercatorUnprojectorTest(){
        Node nd = MercatorProjector.unproject(0.0, -7.081154551613622E-10);
        assertEquals(0.0, nd.getLon());
        assertEquals(0.0, nd.getLat());
    }

    @Test
    public void extremeMercatorUnprojectorTest(){
        Node nd = MercatorProjector.unproject(4.0075016E7, 2.38107696E8);
        assertEquals(360, nd.getLon());
        assertEquals(90, nd.getLat());
    }
}
