package com.tunemate.be.domain.auth.controller;

import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuth;
import com.tunemate.be.domain.auth.domain.emailAuth.dto.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.service.EmailAuthService;
import com.tunemate.be.global.responses.OkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth/email")
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    public EmailAuthController(EmailAuthService emailAuthService) {
        this.emailAuthService = emailAuthService;
    }


    @PostMapping()
    public ResponseEntity<OkResponse<Void>> CreateEmailAuth(@RequestBody CreateEmailAuthDTO dto) throws ExecutionException, InterruptedException {
        emailAuthService.createOrUpdateEmailAuth(dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    @PostMapping("/verify")
    public ResponseEntity<OkResponse<EmailAuth>> VerifyEmailAuth(@RequestParam String token) {
        return ResponseEntity.ok(new OkResponse<>(true, emailAuthService.verifyEmailAuth(token)));
    }
}
