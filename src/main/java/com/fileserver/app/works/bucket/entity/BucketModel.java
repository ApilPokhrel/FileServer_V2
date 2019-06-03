package com.fileserver.app.works.bucket.entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class BucketModel {
    @Size(min=6,message = "Name is required! at least 6 word")
     String name;

     String owners;

    @Size(min=1,message = "Must Provide Data Usage")
     String threshold ; //maximum amount of data this bucket can hold in GB

    @NotEmpty(message = "Methods Cannot be Empty")
    String allowed_methods;

    @NotEmpty(message = "File Type Cannot Be Empty")
    String allowed_file_type;//allow upload only to specified file type eg:(image, video, document)

    public String getAllowed_methods() {
        return allowed_methods;
    }

    public void setAllowed_methods(String allowed_methods) {
        this.allowed_methods = allowed_methods;
    }

    public String getAllowed_file_type() {
        return allowed_file_type;
    }

    public void setAllowed_file_type(String allowed_file_type) {
        this.allowed_file_type = allowed_file_type;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }


}
