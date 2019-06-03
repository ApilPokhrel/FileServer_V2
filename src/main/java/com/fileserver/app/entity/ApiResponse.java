package com.fileserver.app.entity;

import java.util.ArrayList;

public class ApiResponse {

    private String title;
    private ArrayList<String> message;
    private String type;
    private int code;

    public ApiResponse(String title, ArrayList<String> message, String type, int code) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.code = code;
    }

    public ApiResponse() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
