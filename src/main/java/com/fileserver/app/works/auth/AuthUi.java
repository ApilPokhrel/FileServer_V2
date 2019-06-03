package com.fileserver.app.works.auth;


import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.works.user.UserSchema;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class AuthUi {


    private Authenticate auth;

    public AuthUi(Authenticate auth){
      this.auth = auth;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
     public String loginPage(){
        return "login";
    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage(Model model){
        model.addAttribute("user", new UserSchema());
        return "register";
    }


    @RequestMapping(value = "/verification", method = RequestMethod.GET)
    public String verificationPage(Model model) throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }
            auth.isVerifiedUi();
            model.addAttribute("user", auth.getUser());

        return "verification";
    }

    @RequestMapping(value = "/emailInput", method = RequestMethod.GET)
    public String emailInput(){
        return "email-input";
    }

    @RequestMapping(value = "/forgetPass", method = RequestMethod.GET)
    public String forgetPass(Model model) throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }        model.addAttribute("user", auth.getUser());
        return "forgetPass";
    }

}
