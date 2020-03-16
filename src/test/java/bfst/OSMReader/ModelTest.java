package bfst.OSMReader;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import bfst.canvas.Type;

/**
 * ModelTest
 */
public class ModelTest {

    @Test
    public void modelTest() throws FileNotFoundException {
        OSMReader osmReader = new OSMReader(
            getClass().getClassLoader().getResourceAsStream("bfst/tester.osm")
            
            );
        Model model = new Model(osmReader);
        System.out.println(
            model.getAddresses()
            );
            System.out.println(
                model.getBound()
                );

            System.out.println(
                model.getDrawablesOfType(Type.PRIMARY_ROAD)
                );
            

    }
}