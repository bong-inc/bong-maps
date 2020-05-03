package bfst.OSMReader;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ModelTest
 */
public class ModelTest {

    @Test
    public void modelTest() throws FileNotFoundException {
        OSMReader osmReader = new OSMReader(
            getClass().getClassLoader().getResourceAsStream("bfst/noCoastline.osm")
            );
        Model model = new Model(osmReader);


        assertEquals(osmReader.getAddresses(), model.getAddresses());
        assertEquals(osmReader.getBound(), model.getBound());
        assertEquals(osmReader.getGraph(), model.getGraph());
    }
}