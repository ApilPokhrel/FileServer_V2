package com.fileserver.app.handler;

import com.fileserver.app.config.Variables;
import com.fileserver.app.entity.TokenSchema;
import com.fileserver.app.works.user.UserSchema;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTTokenGen {

    Variables variables = new Variables();


    public TokenSchema generateJWTToken(UserSchema user){

        String access = variables.JWT_ACCESS;
        String token = Jwts.builder()
                .claim("_id", user.getId())
                .claim("access", access)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256,variables.JWT_SECRET.getBytes())
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .compact();

        return new TokenSchema(token, access);

    }


    public String verifyJWTToken(String token){
        String _id = null;

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(variables.JWT_SECRET.getBytes())
                    .parseClaimsJws(token);

            _id = (String) claims.getBody().get("_id");
        } catch(Exception ex){}
        return _id;
    }
}
