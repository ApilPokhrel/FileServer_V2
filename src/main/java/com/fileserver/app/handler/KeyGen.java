package com.fileserver.app.handler;

import com.fileserver.app.works.user.UserSchema;
import com.fileserver.app.works.user.entity.ContactModel;
import org.apache.catalina.LifecycleState;

import java.util.*;

public class KeyGen {

    String key;

    public String validToken(String field, int ln, int sum){
        String str = "";

        if(ln <= sum){
            int sub = sum - ln;

            for(int l = 0; l <= sub; l++){
                str += "$";
            }

            field = field+str;

        }
        return field;

    }


    public String getKey(UserSchema user){
        //fields
        String time = String.valueOf(new Date().getTime());
        String id = user.getId();
        String email = user.getContact().get(0).getAddress();


        int t = time.length();
        int i = id.length();
        int e = email.length();


        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(t);
        arr.add(i);
        arr.add(e);

        int sum = Collections.max(arr);


        time = validToken(time, t, sum);
        id = validToken(id, i, sum);
        email = validToken(email, e, sum);

        String str = "";

        for(int l =0; l < sum; l++){



            str += Character.toString(time.charAt(l)) +  Character.toString(id.charAt(l)) +  Character.toString(email.charAt(l));

        }
        key = str.trim();
        return key;

    }



    public void unValid(){
    }

    public String[] decodeKey(String key)throws Exception{

        String keys[] = key.split("\\$");

        int len = keys.length;
        if(len <= 3){
            throw new Exception("Key is not valid");
        }
        String time = keys[len-1];
        String initialKey = keys[0];

        char ke[] = initialKey.toCharArray();
        int sum = ke.length/3;


        ArrayList<Character> ti = new ArrayList<>();
        ArrayList<Character> dd = new ArrayList<>();
        ArrayList<Character> em = new ArrayList<>();

        Thread l1 = new Thread(new Runnable(){
            @Override
            public void run() {
                int l = 0;
                while(l < sum*3){
                    ti.add(ke[l]);
                    l = l+3;
                }
            }
        });

        Thread m1 = new Thread(new Runnable(){
            @Override
            public void run() {
                int m = 1;
                while(m < sum*3){
                    dd.add(ke[m]);
                    m = m+3;
                }
            }


        });


        Thread n1 = new Thread(new Runnable(){
            @Override
            public void run() {
                int n = 2;
                while(n < sum*3){
                    em.add(ke[n]);
                    n = n+3;
                }            }

        });

        l1.start();
        m1.start();
        n1.start();
        l1.join();
        m1.join();
        n1.join();

        String timeAfter = "";
        for(char e: ti){
            timeAfter += Character.toString(e);
        }

        String id ="";
        for(char e: dd){
            id += Character.toString(e);
        }

        String email = "";
        for(char e: em){
            email += Character.toString(e);
        }
        String[] trimmedTime = timeAfter.split("\\$");
        String[] trimmedId = id.split("\\$");
        String[] trimmedEmail =  email.split("\\$");

        if(timeAfter.length() != sum){
            return null;}

        if(!trimmedTime[0].equals(time)){
            return null;}

        String[] all = {trimmedTime[0], trimmedId[0], trimmedEmail[0]};
        return all;
    }

}
