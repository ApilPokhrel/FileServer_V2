package com.fileserver.app.works.user.entity;


import javax.validation.constraints.Size;

public class UserModel {

    @Size(min = 3, message = "Invalid name")
    private String name;

    @Size(min = 8, max=120, message = "Invalid Email")
    private String email;
    private String phone;
    @Size(min = 6, message = "Password must be at least 6 words")
    private String password;
    private String confirm_password;
    private String role;
    private String plan;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}
