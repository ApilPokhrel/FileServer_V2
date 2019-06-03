package com.fileserver.app.works.bucket;

import com.fileserver.app.works.bucket.entity.SaturationModel;
import com.fileserver.app.works.file.FileSchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Document(collection = "buckets")
public class BucketSchema {
    @Id
    private String id;
    private String name;
    private List<String> owners;
    private Map<String, String> permissions;
    private Integer threshold ; //maximum amount of data this bucket can hold in GB
    private List<String> allowed_context_type; // allow upload only to specified extensions(jpeg, mp4, mp3)
    private List<String> allowed_methods;
    private List<String> allowed_file_type;//allow upload only to specified file type eg:(image, video, document)
    private String createAt;
    private SaturationModel saturation;

    @DBRef
    private ArrayList<FileSchema> files;
    private List<String> urls;
    private Integer size_used;
    private ArrayList<String> backupFiles;
    private ArrayList<String> requests;
    private String status;

    public BucketSchema() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, String> permissions) {
        this.permissions = permissions;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public List<String> getAllowed_context_type() {
        return allowed_context_type;
    }

    public void setAllowed_context_type(List<String> allowed_context_type) {
        this.allowed_context_type = allowed_context_type;
    }

    public List<String> getAllowed_methods() {
        return allowed_methods;
    }

    public void setAllowed_methods(List<String> allowed_methods) {
        this.allowed_methods = allowed_methods;
    }

    public List<String> getAllowed_file_type() {
        return allowed_file_type;
    }

    public void setAllowed_file_type(List<String> allowed_file_type) {
        this.allowed_file_type = allowed_file_type;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public ArrayList<FileSchema> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FileSchema> files) {
        this.files = files;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public SaturationModel getSaturation() {
        return saturation;
    }

    public void setSaturation(SaturationModel saturation) {
        this.saturation = saturation;
    }

    public Integer getSize_used() {
        return size_used;
    }

    public void setSize_used(Integer size_used) {
        this.size_used = size_used;
    }

    public ArrayList<String> getBackupFiles() {
        return backupFiles;
    }

    public void setBackupFiles(ArrayList<String> backupFiles) {
        this.backupFiles = backupFiles;
    }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<String> requests) {
        this.requests = requests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FileSchema getFile(String fileName){
        FileSchema fileSchema = null;
        try {
            for (FileSchema file : this.files) {
                System.out.println();
                if (file.getName().equals(fileName)) {
                    fileSchema = file;
                }
            }
        }catch (Exception ex){}
     return fileSchema;
    }
}
