package bfst.OSMReader;

import bfst.canvas.LinePath;
import bfst.canvas.Type;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OSMReaderTest {

    @Test
    public void destroyTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/skelby.osm"));
            r.destroy();

            assertNull(r.getAddresses());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selfclosingCoastlineTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/selfclosingCoastline.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noCoastlineTest() {
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/skelby.osm"));

            assertEquals(1, r.getDrawableByType().get(Type.COASTLINE).size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void northernCoastlineCutoutTest(){
        try {
            OSMReader r = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/NorthernCoastlineCutout.osm"));

            LinePath coastline = (LinePath) r.getDrawableByType().get(Type.COASTLINE).get(0);
            float[] coords = coastline.getCoords();
            Node first = new Node(0, coords[0], coords[1]);
            Node last = new Node(1, coords[coords.length-2], coords[coords.length-1]);

            assertEquals(first.getLon(), last.getLon());
            assertEquals(first.getLat(), last.getLat());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
