package com.fileserver.app.works.user;

import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.DateAndTime;
import com.fileserver.app.handler.Unauthorized;
import com.fileserver.app.works.bucket.BucketInterface;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.user.entity.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/v1/user")
public class UserApi {

    private UserDaoRepository userDaoRepository;
    private Authenticate auth;
    private UserController userController;
    private BucketInterface bucketInterface;
    private DateAndTime dateAndTime;
    @Autowired
    public UserApi(UserDaoRepository userDaoRepository,
                   Authenticate auth,
                   UserController userController,
                   BucketInterface bucketInterface,
                   DateAndTime dateAndTime){
        this.userDaoRepository = userDaoRepository;
        this.auth = auth;
        this.userController = userController;
        this.bucketInterface = bucketInterface;
        this.dateAndTime = dateAndTime;
    }

    @GetMapping("/")
    public ResponseEntity me() throws Exception {
      auth.AuthorizeApi();
      return ResponseEntity.ok(auth.getUser());
    }

    @GetMapping("/list")
    public ResponseEntity list() throws Exception {
        auth.AuthorizeApi();
        return ResponseEntity.ok(userDaoRepository.findAll());
    }

    @PatchMapping("/role/{id}/{role}")
    public ResponseEntity editRole(@PathVariable("role") String role,
                                   @PathVariable("id") String id) throws Exception {
        auth.AuthorizeApi();
        userDaoRepository.findOneAndUpdate("_id", id, "role.role", role, "set");
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/")
    public ResponseEntity update(UserModel userModel) throws IOException, Unauthorized {
        auth.AuthorizeApi();
        UserSchema user = null;
        try{
         user = userController.update(auth.getUser(), userModel);
        }catch (Exception ex){
         return ResponseEntity.status(400).body(ex.getMessage());
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/keys")
    public ResponseEntity getKeys() throws Exception {
        auth.AuthorizeApi();
        ArrayList<String> keys;
        try{
            keys = auth.getUser().getKey();
        }catch (Exception ex){
            return ResponseEntity.status(400).body(false);
        }
        return ResponseEntity.ok(keys);
    }

    @PostMapping("/key")
    public ResponseEntity generateKey() throws Exception {
        auth.AuthorizeApi();
        UserSchema user;
        try {
            user = userDaoRepository.generateKey(auth.getUser());
            for(BucketSchema bucketSchema: user.getBuckets()){
                bucketInterface.addOwner(bucketSchema, user.getKey().get(user.getKey().size()-1));
            }
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex);
        }

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/key/{key}")
    public ResponseEntity deleteKey(@PathVariable("key") String key) throws Exception {
        auth.AuthorizeApi();
        if(auth.getUser().getKey().size() < 2){
            return ResponseEntity.status(400).body("Must Have at least One Key");
        }
        UserSchema user;
        try {
            user = userDaoRepository.deleteKey(auth.getUser(), key);
            for (BucketSchema bucketSchema : user.getBuckets()) {
                bucketInterface.removeOwner(bucketSchema, key);
            }
        }catch (Exception ex){
            return ResponseEntity.status(400).body(false);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity user(@PathVariable("id") String id) throws Exception {
        auth.AuthorizeApi();
        return ResponseEntity.ok(userDaoRepository.findById(id));
    }


    @PatchMapping("/admin/{id}")
    public ResponseEntity userUpdateByRole(@PathVariable("id") String id, UserModel userModel) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeRoleAPi(auth.getUser(), "admin");
        UserSchema user = null;
        try{
             userDaoRepository.findOneAndUpdate("_id", id, "name", userController.setName(userModel.getName()), "set");
            if(userController.validateContact(userModel.getEmail()).equalsIgnoreCase("email")) {
                userDaoRepository.findOneAndUpdate("_id", id, "contact.0.address", userModel.getEmail(), "set");
            }
            if(userController.validateContact(userModel.getPhone()).equalsIgnoreCase("email")) {
                userDaoRepository.findOneAndUpdate("_id", id, "contact.1.address", userModel.getPhone(), "set");
            }
            userDaoRepository.findOneAndUpdate("_id", id, "role.role", userModel.getRole(), "set");
            userDaoRepository.findOneAndUpdate("_id", id, "extras.plan", userModel.getPlan(), "set");
            user = userDaoRepository.findOneAndUpdate("_id", id, "extras.plan_dates", dateAndTime.isoTimeNow() , "push");
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok(user);
    }


    @PatchMapping("/status/{id}")
    public ResponseEntity updateStatus(@PathVariable("id") String id) throws IOException, Unauthorized {
        auth.AuthorizeApi();
        UserSchema userSchema = userDaoRepository.findById(id);
        if(userSchema == null){
            return ResponseEntity.status(404).body("user not found");
        }
        try {
            if (userSchema.getStatus().equalsIgnoreCase("active")) {
                userDaoRepository.findOneAndUpdate("_id", userSchema.getId(), "status", "inactive", "set");
                userDaoRepository.findOneAndUpdate("_id", userSchema.getId(), "keys", new ArrayList<>(), "set");
            }

            if (userSchema.getStatus().equalsIgnoreCase("inactive")) {
                userDaoRepository.findOneAndUpdate("_id", userSchema.getId(), "status", "active", "set");
            }

        }catch (Exception ex){
            return ResponseEntity.status(404).body("user not found");
        }

        return ResponseEntity.ok(userSchema);
    }





}
