package com.example.sns.configuration;

import com.example.sns.configuration.filter.JwtTokenFilter;
import com.example.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String key;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll() // 해당 주소 허용
                .antMatchers("/api/**").authenticated() // /api/** 경로를 authenticated 해준다
                .and()
                .sessionManagement()    // 세션은 따로 관리 안할거다.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()  // http request됐을 때 시큐리티 설정 - 필터를 둬서 들어온 토큰이 어떤 유저가 들어왔는지 토큰을 확인
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
        ;
        // TODO
        //.exceptionHandling()
        //.authenticationEntryPoint()
    }
}
