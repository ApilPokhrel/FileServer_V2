package com.fileserver.app.handler;

import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.user.UserDaoRepository;
import com.fileserver.app.works.user.UserRepository;
import com.fileserver.app.works.user.UserSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@Service
public class Authenticate {
    private HttpServletRequest req;
    private HttpServletResponse res;
    private UserDaoRepository userDaoRepository;
    private WebStorage webStorage;

    @Autowired
    public Authenticate(HttpServletResponse res,
                        HttpServletRequest req,
                        UserDaoRepository userDaoRepository,
                        WebStorage webStorage) {
        this.userDaoRepository = userDaoRepository;
        this.req = req;
        this.res = res;
        this.webStorage = webStorage;

    }

    public void AuthorizeUi() throws UnauthorizedUi, IOException {
            String token = webStorage.getToken(req, "access_token");
            UserSchema user = userDaoRepository.findByTokens(token);
            if (user == null) {
                throw new UnauthorizedUi();
            } else {
                if(!user.getVerified()){
                   res.sendRedirect("/verification");
                }
                if(!user.getRole().getRole().equalsIgnoreCase("admin") && user.getStatus().equalsIgnoreCase("inactive")){
                    throw new UnauthorizedUi();
                }
                req.setAttribute("user", user);
            }

    }

    public UserSchema getUserLocal(){
        String token = webStorage.getToken(req, "access_token");
        return userDaoRepository.findByTokens(token);
    }



    public void AuthorizeApi() throws IOException, Unauthorized {
        String token = req.getHeader("access_token");
            UserSchema user = userDaoRepository.findByTokens(token);
            if (user == null) {
//                res.setStatus(400);
//                PrintWriter out = res.getWriter();

                //create Json Object
                // finally output the json string
//                out.print("failed");
               throw new Unauthorized();
            }
            else{
                if(!user.getRole().getRole().equalsIgnoreCase("admin") && user.getStatus().equalsIgnoreCase("inactive")){
                    throw new Unauthorized();
                }
                req.setAttribute("user", user);
                req.setAttribute("token", token);

            }


    }

    public void AuthorizeRoleAPi(UserSchema user, String role) throws Unauthorized {
         if(!user.getRole().getRole().equals(role)){
            throw new Unauthorized();
         }
    }

    public void AuthorizeRoleUi(UserSchema user, String role) throws UnauthorizedUi {
        if(!user.getRole().getRole().equals(role)){
            throw new UnauthorizedUi();
        }
    }



    public UserSchema getUser() throws Exception {
        UserSchema user = null;
        try{
           user = (UserSchema) req.getAttribute("user");
        }catch (NullPointerException ex){
            throw new Exception("user not found");
        }

        return user;
    }


    public void isVerifiedApi() throws Exception {
        boolean is_verified = getUser().getVerified();
        if(is_verified){
            res.setStatus(400);
            PrintWriter out = res.getWriter();
            out.print("failed");
        }
    }


    public void isVerifiedUi() throws Exception {
        boolean is_verified = false;
        try {
             is_verified = getUser().getVerified();
        }catch(Exception ex){
        }
        if(is_verified){
            res.sendRedirect("/");
        }

    }

public ArrayList<String> modelValidation(BindingResult bindingResult){
    if(bindingResult.hasErrors()){
        ArrayList<String> errors = new ArrayList<>();
        for(FieldError error: bindingResult.getFieldErrors()){
            errors.add(error.getDefaultMessage());
        }
        return errors;
    }

    return null;
}


    public void AuthorizeBucketApi(UserSchema user, String bucketName) throws IOException, Unauthorized {
        boolean exist = false;
        BucketSchema bucketSchema = null;
        for(BucketSchema bucket: user.getBuckets()){
            if(bucket.getName().equalsIgnoreCase(bucketName)){
                exist = true;
                bucketSchema = bucket;
            }
        }
        if (exist) {

           req.setAttribute("bucket", bucketSchema);
        }
        else{
           throw new Unauthorized();
        }


    }


    public void AuthorizeBucketUi(UserSchema user, String bucketName) throws IOException {
        boolean exist = false;
        BucketSchema bucketSchema = null;
        for(BucketSchema bucket: user.getBuckets()){
            if(bucket.getName().equalsIgnoreCase(bucketName)){
                exist = true;
                bucketSchema = bucket;
            }
        }
        if (exist){
            req.setAttribute("bucket", bucketSchema);
        }
        else{
            res.sendRedirect("/");
        }


    }


    public BucketSchema getBucket(Boolean showUserKey){
        BucketSchema bucket = new BucketSchema();
        try{
            bucket = (BucketSchema) req.getAttribute("bucket");
            if(!showUserKey){
                bucket.getOwners().remove(0);
            }
        }catch (NullPointerException ex){

        }

        return bucket;
    }




}
