package bong.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileTypeNotSupportedTest {

    @Test
    public void fileTypeNotSupportedTest(){
        try{
            throw new FileTypeNotSupportedException(".filetype");
        } catch (FileTypeNotSupportedException e) {
            assertEquals(".filetype", e.getFileType());
        }
    }
}
