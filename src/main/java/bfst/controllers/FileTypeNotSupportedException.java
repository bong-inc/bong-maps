package bfst.controllers;

public class FileTypeNotSupportedException extends Exception{
    private String fileType;

    public FileTypeNotSupportedException(String fileType){
        this.fileType = fileType;
    }

    public String getFileType(){
        return fileType;
    }
}
