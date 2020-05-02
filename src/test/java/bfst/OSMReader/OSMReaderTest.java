package bfst.OSMReader;

import bfst.canvas.LinePath;
import bfst.canvas.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void parseCityTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/koldingCity.osm"));

        assertEquals("Kolding", reader.getCities().get(0).getName());
        assertEquals(1, reader.getCities().size());

        reader.parseCity("place", "town");
        assertEquals("Kolding", reader.getCities().get(0).getName());
        assertEquals(1, reader.getCities().size());

        reader.parseCity("place", "hamlet");
        assertEquals("Kolding", reader.getCities().get(0).getName());
        assertEquals(1, reader.getCities().size());

        reader.setPreviousName("TestCity");
        reader.parseCity("place", "suburb");
        assertEquals("TestCity", reader.getCities().get(1).getName());
        assertEquals(2, reader.getCities().size());
    }

    @Test
    void parseStreetAccessTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/streetTest.osm"));
        reader.parseStreet();
        boolean firstFound = false;
        boolean secondFound = false;

        for (var a : reader.getGraph().getAdj().entrySet()) {
            for (var b : a.getValue()) {
                if (b.getStreet().getName().equals("RestrictedRoad")) {
                    Assertions.fail();
                }
                if (b.getStreet().getName().equals("NotRestricted1")) {
                    firstFound = true;
                }
                if (b.getStreet().getName().equals("NotRestricted1")) {
                    secondFound = true;
                }
            }
        }
        assertTrue(firstFound && secondFound);
    }

    @Test
    void parseStreetLivingStreetTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/streetTest.osm"));
        reader.parseStreet();

        for (var a : reader.getGraph().getAdj().entrySet()) {
            for (var b : a.getValue()) {
                if (b.getStreet().getName().equals("Horskærvej")) {
                    assertEquals(30, b.getStreet().getMaxspeed());
                }
            }
        }
    }

    @Test
    void parseMotorwayStreetTest() {
        OSMReader reader = new OSMReader(getClass().getClassLoader().getResourceAsStream("bfst/streetTest.osm"));
        reader.parseStreet();

        for (var a : reader.getGraph().getAdj().entrySet()) {
            for (var b : a.getValue()) {
                if (b.getStreet().getName().equals("TestMotorway")) {
                    assertEquals(130, b.getStreet().getMaxspeed());
                }
            }
        }
    }

}
