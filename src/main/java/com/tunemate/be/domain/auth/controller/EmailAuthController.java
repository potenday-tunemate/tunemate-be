package com.tunemate.be.domain.auth.controller;

import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.service.EmailAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    public EmailAuthController(EmailAuthService emailAuthService) {
        this.emailAuthService = emailAuthService;
    }

    @GetMapping("/email")
    public ResponseEntity<EmailAuth> FindEmailAuthByToken() {
        return ResponseEntity.ok(emailAuthService.getEmailAuthByToken("test"));
    }
}
