package com.fileserver.app.works.file;


import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.Unauthorized;
import com.fileserver.app.works.bucket.BucketInterface;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.file.entity.FileModel;
import com.fileserver.app.works.network.RequestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/file")
public class FileApi {


    private Authenticate auth;
    private FileController fileController;
    private BucketInterface bucketInterface;

    @Autowired
    public FileApi(Authenticate auth,
                   FileController fileController,
                   BucketInterface bucketInterface){
       this.auth = auth;
       this.fileController = fileController;
       this.bucketInterface = bucketInterface;
    }

    @PostMapping("/{url}")
    public ResponseEntity upload(@PathVariable("url") String url,
                                 @Valid FileModel fileModel,
                                 BindingResult bindingResult
                                 ){
        BucketSchema bucketSchema = null;
        ArrayList<String> errors = auth.modelValidation(bindingResult);
        if(errors != null){
            return ResponseEntity.status(400).body(errors);
        }

        try{
            bucketSchema = fileController.validateUrl(url);
        }catch (Exception ex){
            return ResponseEntity.status(403).body("Invalid url");
        }

        bucketInterface.removeUrl(bucketSchema, url);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/url/{bucket}/{key}/{fileName}")
    public ResponseEntity getSignUrl(@PathVariable("bucket") String bucketName,
                                     @PathVariable("key") String key,
                                     @PathVariable("fileName") String fileName){
        BucketSchema bucketSchema = null;
        String url;
        try{
         bucketSchema = fileController.AuthorizeKey(key, bucketName);
        }catch (Exception ex){
            return ResponseEntity.status(403).body(ex.getMessage());
        }

        if(bucketSchema.getStatus().equalsIgnoreCase("inactive")){
            return ResponseEntity.status(400).body("Your bucket is under black list");
        }

        if(bucketSchema == null){
            return ResponseEntity.status(403).body("Unauthorized Bucket");
        }else{
            String uuid = UUID.randomUUID().toString().replace("-", "");
            url = bucketName+"-"+uuid+"-"+fileName;
            bucketInterface.addUrl(bucketSchema, url);
        }
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/{key}/{bucketName}/{fileName}")
    public ResponseEntity deleteFileWithKey(@PathVariable("key") String key,
                                            @PathVariable("bucketName") String bucketName,
                                            @PathVariable("fileName") String fileName,
                                            HttpServletRequest request){
        BucketSchema bucketSchema = null;
        try{
            bucketSchema = fileController.AuthorizeKey(key, bucketName);
            bucketSchema = fileController.deleteFileByName(bucketSchema.getName(), fileName);



            fileController.setRequest(bucketSchema, request);

        }catch (Exception ex){
            return ResponseEntity.status(403).body(ex.getMessage());
        }
        return ResponseEntity.ok(bucketSchema);
    }

    @DeleteMapping("/{bucketId}/{fileId}")
    public ResponseEntity deleteFile(@PathVariable("bucketId") String bucketId,
                                     @PathVariable("fileId") String fileId,
                                     HttpServletRequest request) throws IOException, Unauthorized {
        auth.AuthorizeApi();
       BucketSchema bucketSchema = null;
        try{
          bucketSchema = fileController.deleteFileById(bucketId, fileId);
          fileController.setRequest(bucketSchema, request);

        }catch (Exception ex){
            return ResponseEntity.status(403).body(ex.getMessage());
        }
        return ResponseEntity.ok(bucketSchema);
    }

}
