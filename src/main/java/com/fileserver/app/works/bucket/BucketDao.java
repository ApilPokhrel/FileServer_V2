package com.fileserver.app.works.bucket;


import com.fileserver.app.works.file.FileSchema;
import com.fileserver.app.works.network.RequestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;



import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.fields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;


@Repository
public class BucketDao implements BucketInterface{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BucketSchema create(BucketSchema bucket) throws Exception {

        BucketSchema bucketSchema = mongoTemplate.findOne(new Query(Criteria.where("name").is(bucket.getName())), BucketSchema.class);
        if(bucketSchema != null){
             throw new Exception("Name Already Taken");
        }

        bucketSchema = mongoTemplate.save(bucket);
        return bucketSchema;
    }


    @Override
    public BucketSchema save(BucketSchema bucketSchema){
        bucketSchema = mongoTemplate.save(bucketSchema);
        return bucketSchema;
    }

    @Override
    public BucketSchema update(BucketSchema bucketSchema){
        Query query = new Query(Criteria.where("name").is(bucketSchema.getName()));
        Update update =  new Update().set("owners", bucketSchema.getOwners())
                                     .set("threshold", bucketSchema.getThreshold())
                                     .set("allowed_methods", bucketSchema.getAllowed_methods())
                                     .set("allowed_file_type", bucketSchema.getAllowed_file_type());
       return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema findOne(String field, String value){
        Query query = new Query(Criteria.where(field).is(value));
        return mongoTemplate.findOne(query, BucketSchema.class);
    }

    @Override
    public List<BucketSchema> findAll(int limit, long skip){
        Query query = new Query();
        query.limit(limit);
        query.skip(skip);
        return mongoTemplate.find(query, BucketSchema.class);
    }

    @Override
    public BucketSchema findOneAndUpdate(String field, String value, String updateField, Object updateValue, String type){
        Query query = new Query(Criteria.where(field).is(value));
        Update update = new Update();
        if(type.equalsIgnoreCase("set")) update.set(updateField, updateValue);
        if(type.equalsIgnoreCase("push")) update.push(updateField, updateValue);
        if(type.equalsIgnoreCase("pull")) update.pull(updateField, updateValue);
        if(type.equalsIgnoreCase("addtoset")) update.addToSet(updateField, updateValue);

        return mongoTemplate.findAndModify(query, update,new FindAndModifyOptions().returnNew(true), BucketSchema.class);
    }

    @Override
    public BucketSchema addUrl(BucketSchema bucketSchema, String url){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().addToSet("urls", url);
        return mongoTemplate.findAndModify(query, update,new FindAndModifyOptions().returnNew(true), BucketSchema.class);

    }

    @Override
    public BucketSchema removeUrl(BucketSchema bucketSchema, String url){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().pull("urls", url);
        return mongoTemplate.findAndModify(query, update,new FindAndModifyOptions().returnNew(true), BucketSchema.class);

    }


    @Override
    public BucketSchema findOneAndSet(String find_field, String find_value, String set_field, String set_value, String set_value_type){
        if(set_value_type.equalsIgnoreCase("int")){
           Integer.parseInt(set_value);
        }
        if(set_value_type.equalsIgnoreCase("boolean")){
            Boolean.parseBoolean(set_value);
        }
        Query query = new Query(Criteria.where(find_field).is(find_value));
        Update update = new Update().set(set_field, set_value);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }


    @Override
    public BucketSchema updateStat(BucketSchema bucketSchema, int stat){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().set("saturation.stat", stat);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema updateFile_Count(BucketSchema bucketSchema, int file_count){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().set("saturation.file_count_stat", file_count);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema updateGap(BucketSchema bucketSchema, int gap){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().set("saturation.gap", gap);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema addFile(BucketSchema bucketSchema, FileSchema fileSchema){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().addToSet("files", fileSchema);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }


    @Override
    public BucketSchema set_size_used(BucketSchema bucketSchema, int size_used){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().set("size_used", size_used);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema findFile(String bucketName, String fileId){
         Query query = new Query(Criteria.where("name").is(bucketName));

//                 .and("files._id").is(fileId));
//         query.fields().include("name").position("files", 1);
         BucketSchema bucketSchema = mongoTemplate.findOne(query, BucketSchema.class);
        return bucketSchema;
    }

    @Override
    public BucketSchema removeOwner(BucketSchema bucketSchema, String owner){
      Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
      Update update = new Update().pull("owners", owner);
      return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema addOwner(BucketSchema bucketSchema, String owner){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().addToSet("owners", owner);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }


    @Override
    public BucketSchema removeFile(BucketSchema bucketSchema, FileSchema fileSchema){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().pull("files", fileSchema);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }


    @Override
    public BucketSchema addBackupFile(BucketSchema bucketSchema, String fileId){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().addToSet("backupFiles", fileId);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    @Override
    public BucketSchema removeBackupFile(BucketSchema bucketSchema, String fileId){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().pull("backupFiles", fileId);
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }

    public List<BucketSchema> getAllStorageStat(){
        Aggregation aggregation = newAggregation(group(fields().and("threshold")).sum("threshold").as("threshold"));
        AggregationResults<BucketSchema> result =
                 mongoTemplate.aggregate(aggregation, "buckets",BucketSchema.class);
        List<BucketSchema> resultList = result.getMappedResults();
        return resultList;
    }

    @Override
    public BucketSchema setRequest(BucketSchema bucketSchema, RequestSchema requestSchema){
        Query query = new Query(Criteria.where("_id").is(bucketSchema.getId()));
        Update update = new Update().addToSet("requests", requestSchema.getId());
        return mongoTemplate.findAndModify(query, update, BucketSchema.class);
    }


}
