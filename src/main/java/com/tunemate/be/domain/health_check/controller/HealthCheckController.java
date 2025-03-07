package com.tunemate.be.domain.health_check.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health_check")
public class HealthCheckController {
    @GetMapping
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
