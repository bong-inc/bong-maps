package bfst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.Test;

import bfst.controllers.FileTypeNotSupportedException;
import bfst.controllers.MainController;
import javafx.stage.Stage;

/**
 * MainController
 */
public class MainControllerTest {

  @Test
  public void testMainControllerThrowsException(){
    FileTypeNotSupportedException e = assertThrows(FileTypeNotSupportedException.class, () -> {
        File file = new File("build.gradle");
        new MainController(new Stage()).loadFile(file);
    });

    String expected = ".gradle";
    String actual = e.getFileType();
    assertEquals(expected, actual);
}

  @Test
  public void mainControllerSmokeTest() {
    new MainController(new Stage());
  }
}
