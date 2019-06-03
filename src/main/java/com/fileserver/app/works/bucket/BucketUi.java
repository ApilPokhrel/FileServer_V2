package com.fileserver.app.works.bucket;


import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.KeyGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BucketUi {


    private Authenticate auth;
    private BucketInterface bucketInterface;

    @Autowired
    public BucketUi(Authenticate auth, BucketInterface bucketInterface) {
        this.auth = auth;
        this.bucketInterface = bucketInterface;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }        model.addAttribute("user", auth.getUser());
        return "index";
    }

    @RequestMapping(value = "/bucket/list", method = RequestMethod.GET)
    public String list(Model model) throws Exception {
            auth.AuthorizeUi();
            model.addAttribute("user", auth.getUser());
            return "bucket/list";
    }

    @RequestMapping(value = "/tutorial", method = RequestMethod.GET)
    public String tutorial(Model model) throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }        model.addAttribute("user", auth.getUser());
        return "tutorial";
    }

    @RequestMapping(value = "/bucket/create", method = RequestMethod.GET)
    public String create(Model model) throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }        model.addAttribute("user", auth.getUser());
        return "bucket/create";
    }



    @RequestMapping(value = "/bucket/edit/{bucket}", method = RequestMethod.GET)
    public String edit(@PathVariable("bucket")String bucket, Model model) throws Exception {
            auth.AuthorizeUi();
            model.addAttribute("user", auth.getUser());
            model.addAttribute("bucket", bucketInterface.findOne("_id", bucket));
            return "bucket/edit";
    }

    @RequestMapping(value = "/bucket/{bucket}", method = RequestMethod.GET)
    public String index(@PathVariable("bucket") String bucketName,
                        Model model) throws Exception {
        try {
            auth.AuthorizeUi();
        }catch (Exception ex){
            return "redirect:/login";
        }        auth.AuthorizeBucketUi(auth.getUser(), bucketName);
        model.addAttribute("user", auth.getUser());
        BucketSchema bucketSchema = auth.getBucket(false);
        if (bucketSchema == null) {
            return "index";
        } else {
            model.addAttribute("bucket", bucketSchema);
            return "bucket/detail";
        }
    }



}
