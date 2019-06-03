package com.fileserver.app.works.user;

import com.fileserver.app.entity.TokenSchema;
import com.fileserver.app.handler.ErrorVariables;
import com.fileserver.app.handler.JWTTokenGen;
import com.fileserver.app.handler.KeyGen;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.settings.PlanSchema;
import com.fileserver.app.works.user.entity.ContactModel;
import com.fileserver.app.works.user.entity.NameModel;
import com.mongodb.client.result.UpdateResult;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Repository
public class UserDao implements UserDaoRepository {

    private MongoTemplate mongoTemplate;
    private JWTTokenGen jwtTokenGen;

     ErrorVariables error = new ErrorVariables();
     @Autowired
     public UserDao(MongoTemplate mongoTemplate, JWTTokenGen jwtTokenGen) {
         this.jwtTokenGen = jwtTokenGen;
         this.mongoTemplate = mongoTemplate;

     }

     @Override
     public UserSchema findById(String id){
         return mongoTemplate.findById(id, UserSchema.class);
     }
    @Override
    public UserSchema findByTokens(String token){
              String id = jwtTokenGen.verifyJWTToken(token);
              UserSchema user = null;
              try {
                  return mongoTemplate.findOne(new Query(Criteria.where("_id").is(id))
                                  .addCriteria(Criteria.where("tokens.token").is(token)),
                          UserSchema.class);
              }catch (Exception ex){}
              return user;



    }

    @Override
    public UserSchema findByCredentials(String email, String password) throws Exception {

        UserSchema user = null;
        try {
            user = mongoTemplate.findOne(new Query(Criteria.where("contact.address").is(email)), UserSchema.class);
        } catch(NullPointerException ex){
            throw new Exception(error.USER_NOT_FOUND);
        }
        if(user == null){
            throw new Exception(error.USER_NOT_FOUND);
        }
        if(!BCrypt.checkpw(password, user.getPassword())){
            throw new Exception(error.INVALID_PASSWORD);
        }

        return user;
    }


    @Override
    public UserSchema validateEmail(String email){
        UserSchema userSchema = null;
        try {
            userSchema = mongoTemplate.findOne(new Query(Criteria.where("contact.address").is(email)), UserSchema.class);
        } catch(NullPointerException ex){}
        if(userSchema != null){
            return userSchema;
        }
        return null;
    }





    @Override
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    @Override
    public boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }


    @Override
    public UserSchema add(UserSchema user) throws Exception {

        UserSchema userSchema = mongoTemplate.findOne(new Query(Criteria.where("contact.address").is(user.getContact().get(0).getAddress())), UserSchema.class);
        if(userSchema != null){
            throw new Exception(error.USER_EXIST);
        }
        user.setPassword(hash(user.getPassword()));
        ArrayList<TokenSchema> tokenSchemaArrayList = new ArrayList<>();
        user = mongoTemplate.save(user);

        //SET JWT TOKEN
        TokenSchema tokenSchema = jwtTokenGen.generateJWTToken(user);
        tokenSchemaArrayList.add(tokenSchema);
        user.setToken(tokenSchemaArrayList);

        //SET GEN KEY
        KeyGen keyGen = new KeyGen();
        ArrayList<String> keys = new ArrayList<>();
        keys.add(keyGen.getKey(user));
        user.setKey(keys);

        mongoTemplate.save(user);
        return user;
    }

    @Override
    public UserSchema deleteToken(String id){
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("tokens", new ArrayList<>());
        return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }

    @Override
    public void deleteById(String id) throws Exception {
        try {
            UserSchema user = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), UserSchema.class);
            if(user.getVerified().equals(false)) {
                mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), UserSchema.class);
            }
        } catch (NullPointerException ex){
            throw new Exception(error.USER_DELETE);
        }
    }


    @Override
    public UserSchema findOne(String field, Object value){
        UserSchema user = null;
        try {
            user = mongoTemplate.findOne(new Query(Criteria.where(field).is(value)), UserSchema.class);
        } catch (Exception ex){}

        return user;
    }


    @Override
    public void addBucket(UserSchema user, BucketSchema bucket){
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().addToSet("buckets",bucket);
        mongoTemplate.updateFirst(query, update, UserSchema.class);
    }

     @Override
    public UserSchema removeBucket(UserSchema user, BucketSchema bucket){
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().pull("buckets", bucket);
        return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }


    @Override
    public UserSchema addBucketBackup(UserSchema user, String bucket){
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().addToSet("bucket_backups",bucket);
        return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }

    @Override
    public UserSchema findByKeys(String key){

        Query query = new Query(Criteria.where("keys").is(key));
        return mongoTemplate.findOne(query, UserSchema.class);

    }

    @Override
    public void updateVerified(String id, Boolean status, String type){
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("verified", status);
        if(type.equals("email")) {
            update = new Update().set("verified", status).set("contact.0.verified", status);
        } else if(type.equals("phone")){
            update = new Update().set("verified", status).set("contact.1.verified", status);
        }

        mongoTemplate.updateFirst(query, update, UserSchema.class);

    }


    @Override
    public TokenSchema generateAuthToken(UserSchema user){
        TokenSchema token = jwtTokenGen.generateJWTToken(user);
        ArrayList<TokenSchema> tokens = new ArrayList<>();
        tokens.add(token);
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().push("tokens", token);
        mongoTemplate.updateFirst(query, update, UserSchema.class);
        return token;
    }



    @Override
    public UserSchema findOneAndUpdate(String field, String value, String updateField, Object updateValue, String type){
        Query query = new Query(Criteria.where(field).is(value));
        Update update = new Update();
        if(type.equalsIgnoreCase("set")) update.set(updateField, updateValue);
        if(type.equalsIgnoreCase("push")) update.push(updateField, updateValue);
        if(type.equalsIgnoreCase("pull")) update.pull(updateField, updateValue);
        if(type.equalsIgnoreCase("addtoset")) update.addToSet(updateField, updateValue);

        return mongoTemplate.findAndModify(query, update,new FindAndModifyOptions().returnNew(true), UserSchema.class);
    }

    @Override
    public UserSchema generateKey(UserSchema user) {
         KeyGen keyGen = new KeyGen();
         Query query = new Query(Criteria.where("_id").is(user.getId()));
         Update update = new Update().addToSet("keys", keyGen.getKey(user));
        return mongoTemplate.findAndModify(query, update,
                new FindAndModifyOptions().returnNew(true), UserSchema.class);
    }

    @Override
    public UserSchema deleteKey(UserSchema user, String key) {
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().pull("keys", key);
        return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }

    @Override
    public  UserSchema addContact(String id, ContactModel contactModel){
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().addToSet("contact", contactModel);
        return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }



    @Override
    public UserSchema updateContact(String id, ContactModel contactModel){
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = null;
        if(contactModel.getType().equalsIgnoreCase("email")) {
             update = new Update().set("contact.0", contactModel);
        }
        if(contactModel.getType().equalsIgnoreCase("phone")) {
             update = new Update().set("contact.1", contactModel);
        }
        return mongoTemplate.findAndModify(query, update, UserSchema.class);

    }


    @Override
    public UserSchema updateName(String id, NameModel nameModel){
      Query query = new Query(Criteria.where("_id").is(id));
      Update update = new Update().set("name", nameModel);
      return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }


    @Override
    public UserSchema setPlan(UserSchema userSchema, String id){
       Query query = new Query(Criteria.where("_id").is(userSchema.getId()));
       Update update = new Update().set("extras.plan", id);
       return mongoTemplate.findAndModify(query, update, UserSchema.class);
    }


    @Override
    public List<UserSchema> findAll(){
        return mongoTemplate.findAll(UserSchema.class);
    }


}
