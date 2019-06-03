package com.fileserver.app.works.settings;

import com.fileserver.app.works.user.UserSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanController {


    private PlanInterface planInterface;

    @Autowired
    public PlanController(PlanInterface planInterface){
        this.planInterface = planInterface;
    }

    public PlanSchema setFreePlan(){
        PlanSchema planSchema = new PlanSchema();
        planSchema.setBucket(1);
        planSchema.setName("Basic Plan");
        planSchema.setRequests(1000);
        planSchema.setSize(1);
        planSchema.setTimeGap("month");
        planSchema.setType("free");
        return planSchema;
    }

    public void validatePlanBucket(UserSchema userSchema) throws Exception {
        PlanSchema planSchema = null;
       if(userSchema.getExtras().getPlan().equalsIgnoreCase("free")) planSchema = setFreePlan();
       else planSchema = planInterface.findOne("_id", userSchema.getExtras().getPlan());

       if(planSchema == null) throw new Exception("Choose a Plan");
       if(userSchema.getBuckets() != null) {
           if (userSchema.getBuckets().size() >= planSchema.getBucket()) {
               throw new Exception("Upgrade your plan");
           }
       }

    }

    public void validatePlanRequests(UserSchema userSchema, Integer totalRequests) throws Exception {
        PlanSchema planSchema = null;
        if(userSchema.getExtras().getPlan().equalsIgnoreCase("free")) planSchema = setFreePlan();
        else planSchema = planInterface.findOne("_id", userSchema.getExtras().getPlan());

        if(planSchema == null) throw new Exception("Choose a Plan");

        if(totalRequests >= planSchema.getRequests()){
            throw new Exception("Upgrade your plan");
        }

    }
}
