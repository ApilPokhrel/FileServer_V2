package com.fileserver.app.works.user.entity;

public class PhoneModel {

    private String address;
    private Boolean verified;
    private String type;


    public PhoneModel(String address, Boolean verified, String type) {
        this.address = address;
        this.verified = verified;
        this.type = type;
    }

    public PhoneModel() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
