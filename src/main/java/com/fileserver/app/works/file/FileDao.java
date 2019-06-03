package com.fileserver.app.works.file;


import com.fileserver.app.works.bucket.BucketSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class FileDao implements FileInterface {


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public FileSchema create(FileSchema fileSchema){
        return mongoTemplate.save(fileSchema);
    }

    @Override
    public FileSchema findOne(String field, String value){
     Query query = new Query(Criteria.where(field).is(value));
     return mongoTemplate.findOne(query, FileSchema.class);
    }

}
