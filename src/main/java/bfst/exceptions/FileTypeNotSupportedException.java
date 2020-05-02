package bfst.exceptions;

public class FileTypeNotSupportedException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String fileType;

    public FileTypeNotSupportedException(final String fileType){
        this.fileType = fileType;
    }

    public String getFileType(){
        return fileType;
    }
}
