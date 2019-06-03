package com.fileserver.app.works.settings;

import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.UnauthorizedUi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingsUi {

    private Authenticate auth;
    @Autowired
    public SettingsUi(Authenticate auth){
        this.auth = auth;
    }

    @RequestMapping("/plan/create")
    public String  createPlan() throws Exception {
        auth.AuthorizeUi();
        auth.AuthorizeRoleUi(auth.getUser(), "admin");
        return "settings/create_plan";
    }

    @RequestMapping("/plan")
    public String  plans() throws UnauthorizedUi {
        auth.AuthorizeUi();
        return "settings/list_plan";
    }


}
