package com.fileserver.app.works.bucket;


import com.fileserver.app.entity.Mail;
import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.Unauthorized;
import com.fileserver.app.notification.BucketConflictMail;
import com.fileserver.app.works.bucket.entity.BucketModel;
import com.fileserver.app.works.network.RequestInterface;
import com.fileserver.app.works.settings.PlanController;
import com.fileserver.app.works.user.UserController;
import com.fileserver.app.works.user.UserDaoRepository;
import com.fileserver.app.works.user.UserSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bucket")
public class BucketApi {


    private BucketInterface bucketInterface;
    private Authenticate auth;
    private BucketController bucketController;
    private UserDaoRepository userDaoRepository;
    private PlanController planController;
    private UserController userController;
    private BucketConflictMail bucketConflictMail;
    private RequestInterface requestInterface;
    @Autowired
    public  BucketApi(BucketInterface bucketInterface,
                      Authenticate auth,
                      BucketController bucketController,
                      UserDaoRepository userDaoRepository,
                      PlanController planController,
                      UserController userController,
                      RequestInterface requestInterface,
                      BucketConflictMail bucketConflictMail){
            this.bucketInterface = bucketInterface;
            this.auth = auth;
            this.bucketController = bucketController;
            this.userDaoRepository = userDaoRepository;
            this.planController = planController;
            this.userController = userController;
            this.requestInterface = requestInterface;
            this.bucketConflictMail = bucketConflictMail;
    }

    @PatchMapping("/")
    public ResponseEntity editBucketSpec(@RequestParam(value = "bucketName", required = false) String bucketName,
                                         @RequestParam(value = "updateField", required = false) String updateField,
                                         @RequestParam(value = "updateValue", required = false) String updateValue,
                                         @RequestParam(value = "updateType", required = false) String updateType) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeRoleAPi(auth.getUser(), "admin");
        BucketSchema bucketSchema = null;
        try{
            bucketSchema = bucketInterface.findOneAndUpdate("name", bucketName, updateField, updateValue, "set");
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }

        UserSchema user = userDaoRepository.findOne("buckets", bucketSchema);
        Map<String, String> map = new HashMap<>();

        if(updateField.equalsIgnoreCase("status") && bucketSchema.getStatus().equalsIgnoreCase("active")){
            map.put("username", user.getName().getUser());
            map.put("msg1", "has been activated");
            map.put("msg2", "If Not Activate Please Send Bail request to below EMail");
            map.put("email", "apilpokharel5@gmail.com");
            Mail mail = new Mail();
            mail.setFrom("apilpokharel5@gmail.com");
            mail.setTo(user.getContact().get(0).getAddress());
            mail.setSubject("Bucket Activated");
            mail.setModel(map);
            try {
                bucketConflictMail.sendSimpleMessage(mail);
            }catch(Exception ex){
                //No Message
            }
        }

        if(updateField.equalsIgnoreCase("status") && bucketSchema.getStatus().equalsIgnoreCase("inactive")){
            map.put("username", user.getName().getUser());
            map.put("msg1", "has been under blacklist");
            map.put("msg2", "DO NOT GET PANIC WE WILL GET TO YOU  SOON  SOME VENERABLE TASK OCCUR IN YOUR BUCKET. ");
            map.put("email", "apilpokharel5@gmail.com");
            Mail mail = new Mail();
            mail.setFrom("apilpokharel5@gmail.com");
            mail.setTo(user.getContact().get(0).getAddress());
            mail.setSubject("Bucket Conflict");
            mail.setModel(map);
            try {
                bucketConflictMail.sendSimpleMessage(mail);
            }catch(Exception ex){
                //No Message
            }
        }



        return ResponseEntity.ok(bucketSchema);
    }

    @PostMapping("/create")
    public ResponseEntity create(@Valid  BucketModel bucket,
                                 BindingResult bindingResult) throws IOException, Unauthorized {
        auth.AuthorizeApi();
        ArrayList<String> errors = auth.modelValidation(bindingResult);
        if(errors != null){
            return ResponseEntity.status(400).body(errors);
        }
        try {
            planController.validatePlanBucket(auth.getUser());
            BucketSchema bucketSchema = bucketInterface.create(bucketController.setBucket(bucket, auth.getUser()));

            bucketController.bucketFolder(bucket.getName());
            bucketInterface.updateStat(bucketSchema, 1);
            bucketInterface.updateFile_Count(bucketSchema, 0);
            bucketInterface.updateGap(bucketSchema, 100000);
            userDaoRepository.addBucket(auth.getUser(), bucketSchema);
        }catch (Exception ex){
         return ResponseEntity.status(401).body(ex.getMessage());
        }

        return ResponseEntity.ok(bucket);
    }

    @GetMapping("/stat/storage")
    public ResponseEntity stat() throws IOException, Unauthorized {
        auth.AuthorizeApi();
        return null;
    }

    @GetMapping("/list")
    public ResponseEntity list(@RequestParam(value = "limit", required = false) int limit,
                               @RequestParam(value = "skip", required = false) long skip) throws Exception {
        auth.AuthorizeApi();
//        auth.AuthorizeRoleAPi(auth.getUser(),"admin");
        return ResponseEntity.ok(bucketInterface.findAll(limit, skip));
    }

    @GetMapping("/requests/{bucketName}")
    public ResponseEntity requests(@PathVariable("bucketName") String bucketName) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeBucketApi(auth.getUser(), bucketName);
        ArrayList<String> ids = auth.getBucket(false).getRequests();
        return ResponseEntity.ok(requestInterface.getAllById(ids));
    }

    @GetMapping("/{bucket}")
    public ResponseEntity get(@PathVariable("bucket") String bucketName) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeBucketApi(auth.getUser(), bucketName);
        return ResponseEntity.ok(auth.getBucket(false));
    }

    @GetMapping("/admin/{bucket}")
    public ResponseEntity getByRole(@PathVariable("bucket") String bucketName) throws Exception {
        auth.AuthorizeApi();
        return ResponseEntity.ok(bucketInterface.findOne("name", bucketName));
    }

    @GetMapping("/")
    public ResponseEntity myBucket(){

        return null;
    }


    @DeleteMapping("/{bucket}")
    public ResponseEntity deleteBucket(@PathVariable("bucket") String bucketName) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeBucketApi(auth.getUser(), bucketName);
        userDaoRepository.removeBucket(auth.getUser(), auth.getBucket(false));
        userDaoRepository.addBucketBackup(auth.getUser(), bucketName);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/admin/{bucket}")
    public ResponseEntity editBucketByRole(@Valid  BucketModel bucket,
                                     BindingResult bindingResult,
                                     @PathVariable("bucket") String bucketName) throws Exception {
        auth.AuthorizeApi();
//        auth.AuthorizeRoleAPi(auth.getUser(),"admin");
        ArrayList<String> errors = auth.modelValidation(bindingResult);
        if(errors != null){
            return ResponseEntity.status(400).body(errors);
        }
        System.out.println(bucketName);
        BucketSchema bucketSchema = null;
        try{
            bucketSchema = bucketController.setBucket(bucket, auth.getUser());
            BucketSchema bucketSchema1 = bucketInterface.findOne("name", bucketName);
            bucketSchema1.setName(bucketSchema.getName());
            bucketSchema1.setAllowed_file_type(bucketSchema.getAllowed_file_type());
            bucketSchema1.setAllowed_methods(bucketSchema.getAllowed_methods());
            bucketSchema1.setOwners(bucketSchema.getOwners());
            bucketSchema1.setThreshold(bucketSchema.getThreshold());
            bucketInterface.save(bucketSchema1);
            bucketSchema = bucketSchema1;
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok(bucketSchema);
    }

    @PatchMapping("/{bucket}")
    public ResponseEntity editBucket(@Valid  BucketModel bucket,
                                     BindingResult bindingResult,
                                     @PathVariable("bucket") String bucketName) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeBucketApi(auth.getUser(), bucketName);
        ArrayList<String> errors = auth.modelValidation(bindingResult);
        if(errors != null){
            return ResponseEntity.status(400).body(errors);
        }
        BucketSchema bucketSchema = null;
        try{
          bucketSchema = bucketInterface.update(bucketController.setBucket(bucket, auth.getUser()));
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok(bucketSchema);
    }





}
