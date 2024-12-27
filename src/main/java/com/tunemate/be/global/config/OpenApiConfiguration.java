package com.tunemate.be.global.config;


import com.tunemate.be.global.annotations.Auth;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenApiCustomizer customAuthOpenApi() {
        return openApi -> {
            // openApi 객체를 순회하면서
            // @Auth 붙은 메서드에는 자동으로 security 추가
        };
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            if (handlerMethod.hasMethodAnnotation(Auth.class)) {
                operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            }
            return operation;
        };
    }

}
