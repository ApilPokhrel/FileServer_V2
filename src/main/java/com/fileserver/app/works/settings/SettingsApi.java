package com.fileserver.app.works.settings;

import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.Unauthorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsApi {

    private Authenticate auth;
    private PlanController planController;
    private PlanInterface planInterface;
    @Autowired
    public SettingsApi(Authenticate auth,
                       PlanController planController,
                       PlanInterface planInterface){
        this.auth = auth;
        this.planController = planController;
        this.planInterface = planInterface;
    }


    @PostMapping("/plan")
    public ResponseEntity create(@Valid PlanSchema planSchema,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) throws Exception {
        auth.AuthorizeApi();
        auth.AuthorizeRoleAPi(auth.getUser(), "admin");
        ArrayList<String> errors = auth.modelValidation(bindingResult);
        if(errors != null){
            return ResponseEntity.status(400).body(errors);
        }
        String feature = request.getParameter("feature");
        ArrayList<String> features = new ArrayList();
        for(String f : feature.split(",")){
            features.add(f);
        }
        planSchema.setFeatures(features);
        planSchema = planInterface.add(planSchema);
        return ResponseEntity.ok(planSchema);
    }

    @GetMapping("/plan/{id}")
    public ResponseEntity getPlan(@PathVariable("id") String id) throws IOException, Unauthorized {
        auth.AuthorizeApi();
        if(id.equalsIgnoreCase("free")){
            return ResponseEntity.ok(planController.setFreePlan());
        }else {
            return ResponseEntity.ok(planInterface.findOne("_id", id));
        }
    }


    @GetMapping("/plan")
    public ResponseEntity getPlans() throws IOException, Unauthorized {
        auth.AuthorizeApi();
        return ResponseEntity.ok(planInterface.findAll());
    }
}
