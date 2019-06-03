package com.fileserver.app.handler;

public class UnauthorizedUi extends Exception {

    public UnauthorizedUi(){ super(); }
    public UnauthorizedUi(String message){
        super((message));
    }

}
