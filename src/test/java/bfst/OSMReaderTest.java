package bfst;

import static org.junit.jupiter.api.Assertions.*;

import bfst.controllers.FileTypeNotSupportedException;
import bfst.controllers.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;

import java.io.File;

public class OSMReaderTest extends Application {

    @Test
    public void testOSMReaderThrowsException(){
        FileTypeNotSupportedException e = assertThrows(FileTypeNotSupportedException.class, () -> {
            File file = new File("build.gradle");
            new MainController(new Stage()).loadFile(file);
        });

        String expected = ".gradle";
        String actual = e.getFileType();
        assertEquals(expected, actual);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}
}
