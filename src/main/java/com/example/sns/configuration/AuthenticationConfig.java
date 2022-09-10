package com.example.sns.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll() // 해당 주소 허용
                .antMatchers("/api/**").authenticated() // /api/** 경로를 authenticated 해준다
                .and()
                .sessionManagement()    // 세션은 따로 관리 안할거다.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        // TODO
        //.exceptionHandling()
        //.authenticationEntryPoint()
    }
}
