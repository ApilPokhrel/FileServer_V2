package com.fileserver.app.works.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlanDao implements PlanInterface {


    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public PlanSchema add(PlanSchema planSchema){
        return mongoTemplate.save(planSchema);
    }

    @Override
    public PlanSchema findOne(String field, String value){
        return mongoTemplate.findOne(new Query(Criteria.where(field).is(value)), PlanSchema.class);
    }

    @Override
    public List<PlanSchema> findAll(){
        return  mongoTemplate.findAll(PlanSchema.class);
    }

}
