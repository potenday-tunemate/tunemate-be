package com.tunemate.be.domain.auth.controller;


import com.tunemate.be.domain.auth.domain.auth.SigninDTO;
import com.tunemate.be.domain.auth.domain.auth.SigninResponse;
import com.tunemate.be.domain.auth.service.AuthService;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.global.responses.OkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<OkResponse<SigninResponse>> SigninController(@RequestBody SigninDTO dto) {
        return ResponseEntity.ok(new OkResponse<>(true, authService.signIn(dto)));
    }

    @PostMapping("/signup")
    public ResponseEntity<OkResponse<Void>> SignUpController(@RequestBody CreateUserDTO dto) {
        authService.signUp(dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }
}
