package bfst.controllers;

import bfst.App;
import bfst.canvas.PointOfInterest;
import com.sun.tools.javac.Main;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileControllerTest {
    FileController fileController = new FileController();

    @Test
    public void testLoadBinary() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bfst/POI.bin");
            ArrayList<PointOfInterest> actual = (ArrayList<PointOfInterest>) fileController.loadBinary(is);
            ArrayList<PointOfInterest> expected = new ArrayList<>();

            PointOfInterest poi1 = new PointOfInterest(1400464.0f, -7494234.5f, "Christiansborg");
            PointOfInterest poi2 = new PointOfInterest(1384447.9f, -7482500.0f, "Emils nabo :))");
            PointOfInterest poi3 = new PointOfInterest(1402559.0f, -7493729.0f, "Pusher street");
            expected.add(poi1);
            expected.add(poi2);
            expected.add(poi3);

            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(expected.get(i).toString(), actual.get(i).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadZip() {
        File file = new File(getClass().getClassLoader().getResource("bfst/demozip.zip").getFile());
        try {
            InputStream in = new FileInputStream(fileController.loadZip(file).getPath());
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}