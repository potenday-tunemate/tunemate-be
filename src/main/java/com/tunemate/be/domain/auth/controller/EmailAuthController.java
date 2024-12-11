package com.tunemate.be.domain.auth.controller;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.service.EmailAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/email")
    public ResponseEntity<Void> CreateEmailAuth(@RequestBody CreateEmailAuthDTO dto) {
        emailAuthService.createEmailAuth(dto);
        return ResponseEntity.ok().build();
    }
}
