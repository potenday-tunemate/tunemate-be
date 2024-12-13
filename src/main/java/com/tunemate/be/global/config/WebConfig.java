package com.tunemate.be.global.config;


import com.tunemate.be.global.jwt.JwtAuthenticationFilter;
import com.tunemate.be.global.jwt.JwtTokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthFilterRegistration(JwtTokenProvider jwtTokenProvider) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtTokenProvider));
        registration.addUrlPatterns("/*");
        return registration;
    }
}
