package com.fileserver.app.works.user;

import com.fileserver.app.entity.TokenSchema;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.user.entity.ContactModel;
import com.fileserver.app.works.user.entity.ExtrasModel;
import com.fileserver.app.works.user.entity.NameModel;
import com.fileserver.app.works.user.entity.RoleModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Document(collection = "users")
public class UserSchema {
    @Id
    private String id;
    private NameModel name;
    private String password;
    private ArrayList<ContactModel> contact;
    @DBRef
    private ArrayList<BucketSchema> buckets;
    private RoleModel role;
    @Field("keys")
    private ArrayList<String> keys;
    private Boolean verified = false;
    @Field("tokens")
    private ArrayList<TokenSchema> tokens;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
    private List<String> bucket_backups;
    private ExtrasModel extras;
    private String status;
    public UserSchema() {
        super();
    }

    public UserSchema(String id, NameModel name, String password, ArrayList<ContactModel> contact, ArrayList<BucketSchema> buckets, RoleModel role, ArrayList<String> keys, Boolean verified, ArrayList<TokenSchema> tokens, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.contact = contact;
        this.buckets = buckets;
        this.role = role;
        this.keys = keys;
        this.verified = verified;
        this.tokens = tokens;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NameModel getName() {
        return name;
    }

    public void setName(NameModel name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<ContactModel> getContact() {
        return contact;
    }

    public void setContact(ArrayList<ContactModel> contact) {
        this.contact = contact;
    }

    public ArrayList<BucketSchema> getBuckets() {
        return buckets;
    }

    public void setBuckets(ArrayList<BucketSchema> buckets) {
        this.buckets = buckets;
    }

    public RoleModel getRole() {
        return role;
    }

    public ExtrasModel getExtras() {
        return extras;
    }

    public void setExtras(ExtrasModel extras) {
        this.extras = extras;
    }

    public void setRole(RoleModel role) {
        this.role = role;
    }

    public ArrayList<String> getKey() {
        return keys;
    }

    public void setKey(ArrayList<String> keys) {
        this.keys = keys;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public ArrayList<TokenSchema> getTokens() {
        return tokens;
    }

    public void setToken(ArrayList<TokenSchema> tokens) {
        this.tokens = tokens;
    }

    public List<String> getBucket_backups() {
        return bucket_backups;
    }

    public void setBucket_backups(List<String> bucket_backups) {
        this.bucket_backups = bucket_backups;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
