package com.tunemate.be.global.jwt;


import com.tunemate.be.global.annotations.Auth;
import com.tunemate.be.global.exceptions.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            Auth authAnnotation = handlerMethod.getMethodAnnotation(Auth.class);
            if (authAnnotation == null) {
                authAnnotation = handlerMethod.getBeanType().getAnnotation(Auth.class);
            }

            if (authAnnotation != null) {
                String ID = (String) request.getAttribute("authenticatedUserID");
                if (ID == null) {
                    throw new CustomException("인증되지 않았습니다.", HttpStatus.UNAUTHORIZED, 1002, "");
                }
            }
        }
        return true;
    }
}
