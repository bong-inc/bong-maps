package bfst;

import static org.junit.jupiter.api.Assertions.*;

import bfst.controllers.FileTypeNotSupportedException;
import bfst.controllers.MainController;
import org.junit.jupiter.api.Test;

import java.io.File;

public class OSMReaderTest {

    @Test
    public void testOSMReaderThrowsException(){
        FileTypeNotSupportedException e = assertThrows(FileTypeNotSupportedException.class, () -> {
            File file = new File("build.gradle");
            MainController.loadFile(file);
        });

        String expected = ".gradle";
        String actual = e.getFileType();
        assertEquals(expected, actual);
    }
}
