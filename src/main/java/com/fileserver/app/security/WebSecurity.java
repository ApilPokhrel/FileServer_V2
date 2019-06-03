package com.fileserver.app.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{


    protected void configure(HttpSecurity http) throws Exception{


        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/register", "/login", "/verification","/user","/update_user","/forgetPass","/bucket/create","/user/*", "/user/edit/*").permitAll()
                .antMatchers("/", "api/v1//register", "/api/v1/login", "/api/v1/verification","/user","/update_user","/forgetPass","/api/v1/bucket/create","/api/v1/user/").permitAll()
                .antMatchers("/js/**", "/css/**", "/img/**").permitAll()

                .antMatchers("/user/**").hasAnyRole("USER")



        ;



    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }

}
