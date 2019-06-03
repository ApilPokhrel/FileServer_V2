package com.fileserver.app.handler;

public class Unauthorized extends Exception {

    public Unauthorized(String message){
        super((message));
    }
    public Unauthorized(){
        super();
    }
}
