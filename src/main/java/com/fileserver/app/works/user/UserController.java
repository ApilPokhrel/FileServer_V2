package com.fileserver.app.works.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileserver.app.entity.Mail;
import com.fileserver.app.handler.CryptoTokenGen;
import com.fileserver.app.handler.DateAndTime;
import com.fileserver.app.works.bucket.BucketSchema;
import com.fileserver.app.works.settings.PlanController;
import com.fileserver.app.works.settings.PlanSchema;
import com.fileserver.app.works.user.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserController {

    private UserDaoRepository userDaoRepository;
    private CryptoTokenGen cryptoTokenGen;
    private PlanController planController;
    private DateAndTime dateAndTime;

    @Autowired
   public UserController(UserDaoRepository userDaoRepository, CryptoTokenGen cryptoTokenGen,
                         PlanController planController, DateAndTime dateAndTime){
      this.userDaoRepository = userDaoRepository;
      this.cryptoTokenGen = cryptoTokenGen;
      this.planController = planController;
      this.dateAndTime = dateAndTime;
   }

    public Mail verifyMail(UserModel userModel, String code){

        Mail mail = new Mail();
        mail.setFrom("apilpokharel5@gmail.com");
        mail.setTo(userModel.getEmail());
        mail.setSubject("Verify Mail");

        Map mod = new HashMap();
        mod.put("username", userModel.getName());
        mod.put("title", "Verify Mail");
        mod.put("location", "nepal");
        mod.put("body", "");
        mod.put("code", code);
        mail.setModel(mod);
        mail.setModel(mod);

        return mail;

    }


    public String validateContact(String address){
        if(address.contains("@")){
            return "email";
        }else{
            return "phone";
        }
    };

    public NameModel setName(String name){
        NameModel nameModel = new NameModel();
        List<String> names = Arrays.asList(name.split(" "));
        if(names.size() == 1){
            nameModel.setFirst(names.get(0));
        }
        if(names.size() == 2){
            nameModel.setFirst(names.get(0));
            nameModel.setLast(names.get(1));
        }
        if(names.size() == 3){
            nameModel.setFirst(names.get(0));
            nameModel.setMiddle(names.get(1));
            nameModel.setLast(names.get(2));
        }

        nameModel.setUser(name);
        return nameModel;
    }


    public UserSchema setUser(UserModel userModel){
        UserSchema user = new UserSchema();
        ArrayList<ContactModel> contactModels = new ArrayList<>();
        contactModels.add(this.setContact(userModel.getEmail(), "email", false));
        user.setPassword(userModel.getPassword());
        user.setContact(contactModels);
        user.setName(this.setName(userModel.getName()));
        user.setStatus("active");
        user.setRole(this.setRole("user", new ArrayList<>()));
        if(userModel.getEmail().equalsIgnoreCase("apilpokharel99@gmail.com")){
            user.setRole(this.setRole("admin", new ArrayList<>()));
        }
        user.setExtras(setExtras());
        return  user;
    }

    public UserSchema setUpdateUser(UserModel userModel){
        UserSchema user = new UserSchema();
        user.setName(this.setName(userModel.getName()));
        //contact take over
        user.setRole(new RoleModel(userModel.getRole(), new ArrayList<>()));

        return  user;
    }

    public ContactModel setContact(String address, String addressType, boolean is_verified){
        ContactModel contactModel = new ContactModel();
        contactModel.setAddress(address);
        contactModel.setType(addressType);
        contactModel.setVerified(is_verified);
        return contactModel;
    }

    public ExtrasModel setExtras(){
      ExtrasModel extrasModel = new ExtrasModel();
      extrasModel.setPlan("free");
      ArrayList<String> dates = new ArrayList<>();
      dates.add(dateAndTime.isoTimeNow());
      extrasModel.setPlan_dates(dates);
      return  extrasModel;
    }

   public RoleModel setRole(String role, ArrayList<String> permissions){
        RoleModel roleModel = new RoleModel();
        roleModel.setRole(role);
        roleModel.setPermissions(permissions);
        return roleModel;
   }

    

    public String validateCrypto(String encrypt, String decrypt, String msg) throws Exception {
        try {
            String codeDecrypt = cryptoTokenGen.decrypt(encrypt);

            if (!codeDecrypt.equals(decrypt.trim())) {
                throw new Exception(msg);
            }
        } catch (Exception ex){
            throw new Exception(msg);
        }
        return null;
    }


    public UserSchema findByEmail(String email) throws Exception {
        UserSchema userSchema = userDaoRepository.findOne("contact.address",email);
        if(userSchema == null){
           throw new Exception("Invalid Email");
        }
        return userSchema;
    }

   //changing password forget pass
    public UserSchema findByEmailAndChangePassword(String email, String password) throws Exception {
        if(password.length() < 7){
            throw new Exception("Password should be At least 6 words");
        }
        password = userDaoRepository.hash(password);
      UserSchema user =  userDaoRepository.findOneAndUpdate("contact.address", email,
                                                            "password", password, "set");
      if(user == null){
        throw new Exception("Not Authorized");
      }
      return user;
    }


    public UserSchema update(UserSchema user, UserModel userModel) throws Exception {
        if(userModel.getName().trim().length() < 3){
            throw new Exception("Invalid Name");
        }

        if(user.getContact().size() <= 1){
            userDaoRepository.addContact(user.getId(), this.setContact(userModel.getPhone(), "phone", false));
        }else{
            userDaoRepository.updateContact(user.getId(), this.setContact(userModel.getPhone(), "phone", false));
        }

        user = userDaoRepository.updateName(user.getId(),this.setName(userModel.getName()));

        return user;
    }

    public UserSchema update(UserSchema user, String[] updateField) throws Exception {
        if(user.getName().getUser().trim().length() < 3){
            throw new Exception("Invalid Name");
        }

        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> map = oMapper.convertValue(user, Map.class);

        for(String field: updateField){
           user = userDaoRepository.findOneAndUpdate("_id", user.getId(), field, map.get(field), "set");
        }

        return user;
    }

}
