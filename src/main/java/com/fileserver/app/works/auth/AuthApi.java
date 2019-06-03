package com.fileserver.app.works.auth;


import com.fileserver.app.entity.Mail;
import com.fileserver.app.entity.TokenSchema;
import com.fileserver.app.handler.Authenticate;
import com.fileserver.app.handler.CryptoTokenGen;
import com.fileserver.app.handler.Unauthorized;
import com.fileserver.app.notification.VerifyMail;
import com.fileserver.app.works.user.UserController;
import com.fileserver.app.works.user.UserDaoRepository;
import com.fileserver.app.works.user.UserRepository;
import com.fileserver.app.works.user.UserSchema;

import com.fileserver.app.works.user.entity.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApi {

    private CryptoTokenGen cryptoTokenGen;
    private UserRepository userRepository;
    private UserDaoRepository userDaoRepository;
    private VerifyMail verifyMail;
    private Authenticate auth;
    private UserController userController;

    @Autowired
    public AuthApi(UserRepository userRepository,
                   UserDaoRepository userDaoRepository,
                   VerifyMail verifyMail,
                   CryptoTokenGen cryptoTokenGen,
                   Authenticate auth,
                   UserController userController){
        this.userRepository = userRepository;
        this.userDaoRepository = userDaoRepository;
        this.verifyMail = verifyMail;
        this.cryptoTokenGen = cryptoTokenGen;
        this.auth = auth;
        this.userController = userController;
    }

    String code =  new BigInteger(15, new SecureRandom()).toString(5);

    @PostMapping("/login")
    public ResponseEntity loginPost(@RequestParam(value = "email", required = false)  String email,
                                    @RequestParam(value = "password", required = false)  String password,

                                    HttpServletResponse res) {


        if(email == null || email.length() < 10){
            return ResponseEntity.status(400).body("Invalid Email");
        }
        if(password == null || password.length() < 6){
            return ResponseEntity.status(400).body("Invalid password");
        }
        UserSchema user = null;
        try {
            user = userDaoRepository.findByCredentials(email, password);
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }

        ArrayList<TokenSchema> tokens = new ArrayList<>();
        tokens.add(userDaoRepository.generateAuthToken(user));
        user.setToken(tokens);

        return ResponseEntity.ok().body(user);
    }


    @PostMapping("/register")
    public ResponseEntity registerPost(@Valid UserModel userModel,
                                    BindingResult bindingResult,
                                    HttpServletResponse res){
        if(bindingResult.hasErrors()){
            ArrayList<String> errors = new ArrayList<>();
            for(FieldError error: bindingResult.getFieldErrors()){
              errors.add(error.getDefaultMessage());
            }
            return ResponseEntity.status(400).body(errors);
        }

        UserSchema user = userController.setUser(userModel);

        Mail mail = userController.verifyMail(userModel, code);

        try {
            user = userDaoRepository.add(user);
        }catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }

        try {
            verifyMail.sendSimpleMessage(mail);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }

        return ResponseEntity.ok().body(Arrays.asList(user, cryptoTokenGen.encrypt(code)));

    }


    @PostMapping("/verification")
    public ResponseEntity verification(@RequestParam(value = "code", required = false) String codev,
                                       @RequestParam(value = "ecode", required = false) String encryptCodev,
                                       HttpServletRequest req) throws Exception {
        auth.AuthorizeApi();
        try {
            auth.isVerifiedApi();
            String codeDecrypt = cryptoTokenGen.decrypt(encryptCodev);

            if (!codeDecrypt.equals(codev.trim())) {
                return ResponseEntity.status(400).body("code did not matched");
            }
        } catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }

      try {
          userDaoRepository.updateVerified(auth.getUser().getId(), true, "email");
      }catch (Exception ex){
          return ResponseEntity.status(400).body(ex.getMessage());
      }
       return ResponseEntity.ok(auth.getUser());
    }


    @DeleteMapping("/deleteAccount")
    public ResponseEntity deleteAccount(@RequestParam(value = "id", required = false) String id){

        try {
            auth.isVerifiedApi();
            userDaoRepository.deleteById(id);
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok("success");
    }


    @DeleteMapping(value = "/logout")
    public ResponseEntity logout() throws IOException, Unauthorized {
        auth.AuthorizeApi();
        UserSchema user = null;
        try {
           user = userDaoRepository.deleteToken(auth.getUser().getId());
        }catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok(user);
    }


    @PostMapping("/email")
    public ResponseEntity getEmail(@RequestParam(value = "email", required = false) String email){
         if(email.length() < 6){
             return ResponseEntity.status(400).body("Email is Required");
         }
        UserSchema userSchema = userDaoRepository.findOne("contact.address",email);
        if(userSchema == null){
            return ResponseEntity.status(400).body("User does not exists");
        }
        TokenSchema token = userDaoRepository.generateAuthToken(userSchema);
         UserModel userModel = new UserModel();
         userModel.setEmail(email);
         userModel.setName(userSchema.getName().getUser());
         Mail mail = userController.verifyMail(userModel, code);
        try {
            verifyMail.sendSimpleMessage(mail);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }

//         userDaoRepository.updateVerified(userSchema.getId(), false, "email");
         return ResponseEntity.ok().body(Arrays.asList(userModel,token,cryptoTokenGen.encrypt(code)));
    }

    @GetMapping("/sendCodeAgain/{email}")
    public ResponseEntity sendCodeAgain(@PathVariable("email") String email){

        UserModel userModel = new UserModel();
        userModel.setEmail(email);
        userModel.setName("user");
        Mail mail = userController.verifyMail(userModel, code);
        try {
            verifyMail.sendSimpleMessage(mail);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
        return ResponseEntity.ok(Arrays.asList(userModel, cryptoTokenGen.encrypt(code)));

    }


    @PostMapping("/forgetPass")
    public ResponseEntity forgetPass(@RequestParam(value = "code", required = false) String codev,
                                     @RequestParam(value = "ecode", required = false) String encryptCodev,
                                     @RequestParam(value = "newPassword", required = false) String password,
                                     @RequestParam(value = "email", required = false) String email) throws IOException, Unauthorized {

        auth.AuthorizeApi();
        UserSchema user = null;
        try {
           userController.validateCrypto(encryptCodev, codev, "Wrong Code");
        } catch (Exception ex){
            return ResponseEntity.status(400).body(ex.getMessage());
        }

         try{
            user = userController.findByEmailAndChangePassword(email, password);
         }catch (Exception ex){
             return ResponseEntity.status(400).body(ex.getMessage());
         }

        return ResponseEntity.ok(user);
    }

}
