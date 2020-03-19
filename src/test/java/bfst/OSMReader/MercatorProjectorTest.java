package bfst.OSMReader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MercatorProjectorTest {
    @Test
    public void positiveMercatorProjectorTest(){
        Node nd = MercatorProjector.project(12, 55);
        assertEquals(1335833.875, nd.getLon());
        assertEquals(7326837.5, nd.getLat());
    }

    @Test
    public void negativeMercatorProjectorTest(){
        Node nd = MercatorProjector.project(-12, -55);
        assertEquals(-1335833.875, nd.getLon());
        assertEquals(-7326837.5, nd.getLat());
    }

    @Test
    public void zeroMercatorProjectorTest(){
        Node nd = MercatorProjector.project(0, 0);
        assertEquals(0.0, nd.getLon());
        assertEquals(7.081154551613622E-10, nd.getLat());
    }

    @Test
    public void extremeMercatorProjectorTest(){
        Node nd = MercatorProjector.project(360, 90);
        assertEquals(4.0075016E7, nd.getLon());
        assertEquals(3.4619288E7, nd.getLat());
    }
}
