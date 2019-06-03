package com.fileserver.app.works.bucket;


import com.fileserver.app.config.Variables;
import com.fileserver.app.handler.DateAndTime;
import com.fileserver.app.handler.KeyGen;
import com.fileserver.app.works.bucket.entity.BucketModel;
import com.fileserver.app.works.bucket.entity.SaturationModel;
import com.fileserver.app.works.user.UserSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BucketController {

    Variables variables = new Variables();
    private DateAndTime dateAndTime;

    @Autowired
    public BucketController(DateAndTime dateAndTime){
        this.dateAndTime = dateAndTime;

    }

    public BucketSchema setBucket(BucketModel bucketModel, UserSchema user) throws Exception {
        BucketSchema bucketSchema = null;
        KeyGen keyGen = new KeyGen();
        try {

            List<String> methods = Arrays.asList(bucketModel.getAllowed_methods().split("\\s*,\\s*"));
            if(bucketModel.getName().split(" ").length > 1){
                throw new Exception("Name Cannot have space");
            }
            List<String> file_type = Arrays.asList(bucketModel.getAllowed_file_type().split("\\s*,\\s*"));
            List<String> keys = Arrays.asList(bucketModel.getOwners().split("\\s*,\\s*"));

            bucketSchema = new BucketSchema();
            bucketSchema.setName(bucketModel.getName().toLowerCase());
            bucketSchema.setThreshold(Integer.parseInt(bucketModel.getThreshold()));
            bucketSchema.setAllowed_file_type(file_type);
            bucketSchema.setAllowed_methods(methods);
            bucketSchema.setSaturation(new SaturationModel(100000, 1, 0));
            bucketSchema.setSize_used(0);

            bucketSchema.setCreateAt(dateAndTime.isoTimeNow());

            List<String> owners = new ArrayList<>();
            owners.add(user.getKey().get(user.getKey().size() - 1));
            if(keys != null) {
                for (String key : keys) {

                    if(!key.trim().isEmpty()){
                        System.out.println(keyGen.decodeKey(key));
                        owners.add(key);
                    }
                }
            }
            bucketSchema.setOwners(owners);
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
        return bucketSchema;
    }

    public void bucketFolder(String name) throws Exception {
        name = name.toLowerCase().trim();
        File file = new File(variables.SERVER_FOLDER+name);//Bucket Folder
        File fileSat = new File(variables.SERVER_FOLDER+name+"/"+1);//Saturation Folder
        try{
            file.mkdir();
        }catch(SecurityException ex){
         throw new Exception(ex.getMessage());
        }

        try{
            fileSat.mkdir();
        }catch(SecurityException ex){
            throw new Exception(ex.getMessage());
        }
    }






}
