package com.tunemate.be.domain.auth.controller;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.service.EmailAuthService;
import com.tunemate.be.global.responses.OkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

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
    public ResponseEntity<OkResponse<Void>> CreateEmailAuth(@RequestBody CreateEmailAuthDTO dto) throws ExecutionException, InterruptedException {
        emailAuthService.createOrUpdateEmailAuth(dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<OkResponse<EmailAuth>> VerifyEmailAuth(@RequestParam String token) {
        return ResponseEntity.ok(new OkResponse<>(true, emailAuthService.verifyEmailAuth(token)));
    }
}
