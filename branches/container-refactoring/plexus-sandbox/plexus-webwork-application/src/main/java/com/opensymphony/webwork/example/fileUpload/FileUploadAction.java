package com.opensymphony.webwork.example.fileUpload;

import com.opensymphony.xwork.ActionSupport;

import java.io.File;

public class FileUploadAction extends ActionSupport {
    File file;
    String fileContentType;
    String fileFileName;
    File[] files;
    String[] filesContentType;
    String[] filesFileName;

    public String execute() throws Exception {

        return SUCCESS;
    }

    public String doDefault() throws Exception {
        return INPUT;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public void setFilesContentType(String[] filesContentType) {
        this.filesContentType = filesContentType;
    }

    public void setFilesFileName(String[] filesFileName) {
        this.filesFileName = filesFileName;
    }
}
