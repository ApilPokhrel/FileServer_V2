package com.fileserver.app.works.user.entity;

import java.util.ArrayList;

public class RoleModel {

    private String role = "user"; //user, admin, super
    private ArrayList<String> permissions;

    public RoleModel(String role, ArrayList<String> permissions) {
        this.role = role;
        this.permissions = permissions;
    }

    public RoleModel() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }
}
