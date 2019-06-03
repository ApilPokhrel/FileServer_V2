package com.fileserver.app.works.user;


import com.fileserver.app.entity.TokenSchema;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.user.entity.ContactModel;
import com.fileserver.app.works.user.entity.NameModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDaoRepository {
    UserSchema findById(String id);

    public UserSchema findByTokens(String token);

     UserSchema findByCredentials(String email, String password) throws Exception;

     UserSchema validateEmail(String email);

    String hash(String password);

    boolean verifyHash(String password, String hash);

    UserSchema add(UserSchema user) throws Exception;

     UserSchema deleteToken(String id);

     void deleteById(String id) throws Exception;

     UserSchema findOne(String field, Object value);

     void addBucket(UserSchema user, BucketSchema bucket);

    UserSchema removeBucket(UserSchema user, BucketSchema bucket);

    UserSchema addBucketBackup(UserSchema user, String bucket);

    UserSchema findByKeys(String key);

    void updateVerified(String id, Boolean status, String type);

    TokenSchema generateAuthToken(UserSchema user);


    UserSchema findOneAndUpdate(String field, String value, String updateField, Object updateValue, String type);

    UserSchema generateKey(UserSchema user);

    UserSchema deleteKey(UserSchema user, String key);

    UserSchema addContact(String id, ContactModel contactModel);

    UserSchema updateContact(String id, ContactModel contactModel);

    UserSchema updateName(String id, NameModel nameModel);

    UserSchema setPlan(UserSchema userSchema, String id);

    List<UserSchema> findAll();
}
