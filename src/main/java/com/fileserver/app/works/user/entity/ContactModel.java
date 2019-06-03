package com.fileserver.app.works.user.entity;

import javax.validation.constraints.Size;

public class ContactModel {

    @Size(min = 6, message = "provide a valid email")
    private String address;
    private Boolean verified;
    private String type;


    public ContactModel getEmail(String address ){
        this.address = address;

        return this;
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
