package com.fileserver.app.entity;

import java.util.ArrayList;

public class TokenSchema {
    private String access;
    private String token;

    public TokenSchema(String token, String access) {
        this.access = access;
        this.token = token;
    }

    public TokenSchema() {

    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
