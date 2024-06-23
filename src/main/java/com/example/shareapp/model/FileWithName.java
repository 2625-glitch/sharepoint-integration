package com.example.shareapp.model;

import java.io.InputStream;

public class FileWithName {
    private InputStream fileStream;
    private String fileName;

    public FileWithName(InputStream fileStream, String fileName) {
        this.fileStream = fileStream;
        this.fileName = fileName;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    public String getFileName() {
        return fileName;
    }

}
