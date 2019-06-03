package com.fileserver.app.handler;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class WebStorage {


    public String getToken(HttpServletRequest req, String cookieName){
        String token = null;
        try{

            Cookie[] cookies = req.getCookies();

            for(Cookie cookie: cookies){
                if(cookie.getName().equals(cookieName)){
                    token = cookie.getValue().toString();
                    break;
                }
            }

        }catch (Exception ex){}

        return token;
    }


    public void deleteCookie(HttpServletRequest req, HttpServletResponse res, String cookieName){
        Cookie cookie =  new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }
}
