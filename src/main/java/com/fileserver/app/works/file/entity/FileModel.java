package com.fileserver.app.works.file.entity;

import javax.validation.constraints.Size;

public class FileModel {

    @Size(min = 1, message = "Invalid file name")
    private String name;
    @Size(min = 5, message = "Invalid file type")
    private String type;
    @Size(min = 1, message = "Invalid file size")
    private String size;
    @Size(min = 10, message = "Invalid File")
    private String payload;
    private String[] permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }
}
