package bfst.canvas;

import bfst.OSMReader.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {

    @Test
    void cityTest() {
        City city = new City("testCity", new Node(1,1,1), "hamlet");
        assertEquals(CityType.HAMLET, city.getType());

        city = new City("testCity", new Node(1,1,1), "town");
        assertEquals(CityType.TOWN, city.getType());

        city = new City("testCity", new Node(1,1,1), "city");
        assertEquals(CityType.CITY, city.getType());

        city = new City("testCity", new Node(1,1,1), "bruh");
        assertEquals(CityType.OTHER, city.getType());

    }

}