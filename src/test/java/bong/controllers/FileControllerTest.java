package bong.controllers;

import bong.canvas.PointOfInterest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

class FileControllerTest {
    FileController fileController = new FileController();

    @Test
    public void testLoadBinary() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bong/POI.bin");
            ArrayList<PointOfInterest> actual = (ArrayList<PointOfInterest>) FileController.loadBinary(is);
            ArrayList<PointOfInterest> expected = new ArrayList<>();

            PointOfInterest poi1 = new PointOfInterest(1392929.9f, -7496614.0f, "punkt");
            PointOfInterest poi2 = new PointOfInterest(1403347.4f, -7486917.5f, "punkt2");
            PointOfInterest poi3 = new PointOfInterest(999491.44f, -7472447.0f, "punkt3");
            expected.add(poi1);
            expected.add(poi2);
            expected.add(poi3);

            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(expected.get(i).toString(), actual.get(i).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    public void testLoadZip() {
        File file = new File(getClass().getClassLoader().getResource("bong/demozip.zip").getFile());
        try {
            InputStream in = new FileInputStream(FileController.loadZip(file).getPath());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ArrayList<String> expected = new ArrayList<>();
            expected.add("linje 1");
            expected.add("linje 2");
            expected.add("linje 3");

            String line;
            int i = 0;
            while ((line = (br.readLine())) != null) {
                Assertions.assertEquals(expected.get(i), line);
                i++;
            }
            
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

}