package bfst.controllers;

import bfst.canvas.PointOfInterest;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PointsOfInterestControllerTest {

    PointsOfInterestController poic;
    PointOfInterest poi;

    @BeforeEach
    void setUp() {
        poic = new PointsOfInterestController();
        poi = new PointOfInterest(1,3, "test");
        poic.addToPOI(poi);
    }

    @Test
    void addToPOI() {
        var actual = PointsOfInterestController.getPointsOfInterest().get(0);
        Assertions.assertEquals(poi, actual);
    }

    @Test
    void setPOI() {
        ArrayList<PointOfInterest> newList = new ArrayList<>();
        poic.setPOI(newList);

        Assertions.assertEquals(0, PointsOfInterestController.getPointsOfInterest().size());
    }

    @Test
    void removePOI() {
        PointOfInterest poi2 = new PointOfInterest(2, 2, "test2");
        poic.addToPOI(poi2);
        poic.removePOI(1, 3);

        Assertions.assertEquals(1, PointsOfInterestController.getPointsOfInterest().size());
        Assertions.assertEquals(poi2, PointsOfInterestController.getPointsOfInterest().get(0));
    }

    @Test
    void POIContains() {
        Assertions.assertEquals(true, poic.POIContains(1,3));
        Assertions.assertEquals(false, poic.POIContains(2,2));
    }

    @Test
    void savePointsOfInterest() {
        poic.savePointsOfInterest();
        long currTime = System.currentTimeMillis();

        try {
            InputStream is = new FileInputStream(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin");
            ArrayList<PointOfInterest> list = (ArrayList<PointOfInterest>) FileController.loadBinary(is);
            Assertions.assertEquals(poi.getName(), list.get(0).getName());

            File file = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin");
            if (file.lastModified() + 10000 < currTime) {
                Assertions.fail();
            }
        } catch (Exception e) {
            Assertions.fail();
            e.printStackTrace();
        }
    }

    @Test
    void addPointOfInterest() {
        poic.addPointOfInterest(new Point2D(3,4), java.util.Optional.of("testName"));
        Assertions.assertEquals(PointsOfInterestController.getPointsOfInterest().get(1).getName(), "testName");
        Assertions.assertEquals(PointsOfInterestController.getPointsOfInterest().get(1).getLon(), 3);
        Assertions.assertEquals(PointsOfInterestController.getPointsOfInterest().get(1).getLat(), 4);

        Optional<String> optional = Optional.empty();
        poic.addPointOfInterest(new Point2D(5,4), optional);
        Assertions.assertEquals(2, PointsOfInterestController.getPointsOfInterest().size());
    }

    @Test
    void loadPointsOfInterest() {
        savePointsOfInterest();
        poic.loadPointsOfInterest();
        Assertions.assertEquals(1, PointsOfInterestController.getPointsOfInterest().size());
        Assertions.assertEquals(poi.getName(), PointsOfInterestController.getPointsOfInterest().get(0).getName());
        Assertions.assertEquals(poi.getLat(), PointsOfInterestController.getPointsOfInterest().get(0).getLat());
        Assertions.assertEquals(poi.getLon(), PointsOfInterestController.getPointsOfInterest().get(0).getLon());

        try {
            File file = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "POI.bin");
            file.delete();
            poic.loadPointsOfInterest();
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}