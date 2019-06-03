package com.fileserver.app.works.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RequestDao implements RequestInterface{

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public RequestSchema add(RequestSchema requestSchema){
        return mongoTemplate.save(requestSchema);
    }

    @Override
    public List<RequestSchema> getAllById(ArrayList<String> ids){
         List<RequestSchema> requestSchemas = new ArrayList<RequestSchema>();

             for (String id : ids) {

                 RequestSchema requestSchema = null;
                 try {
                     requestSchema = mongoTemplate.findById(id, RequestSchema.class);
                 }catch (Exception ex){}

                 if(requestSchema != null){ requestSchemas.add(requestSchema);}

             }

         return requestSchemas;
    }

    @Override
    public RequestSchema findOne(String field, String value){
        Query query = new Query(Criteria.where(field).is(value));
        return mongoTemplate.findOne(query, RequestSchema.class);
    }
}
