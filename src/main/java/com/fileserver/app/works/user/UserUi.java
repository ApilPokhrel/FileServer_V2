package com.fileserver.app.works.user;


import com.fileserver.app.handler.Authenticate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import java.io.IOException;

@Controller

@RequestMapping("/user")
public class UserUi {


    @Autowired
    private Authenticate auth;

    @Autowired
    private UserDaoRepository userDaoRepository;

    @RequestMapping("/")
    public String detail() throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }
        return "user/detail";

    }

    @RequestMapping("/list")
    public String list() throws Exception {
        auth.AuthorizeUi();
        return "user/list";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id,
                       Model model) throws Exception {
        auth.AuthorizeUi();
        model.addAttribute("user", userDaoRepository.findById(id));
        return "user/edit";
    }
}
