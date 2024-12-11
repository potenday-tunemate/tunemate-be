package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.domain.EmailAuthMapper;
import org.springframework.stereotype.Service;

@Service
public class EmailAuthService {
    private final EmailAuthMapper emailAuthMapper;

    public EmailAuthService(EmailAuthMapper emailAuthMapper) {
        this.emailAuthMapper = emailAuthMapper;
    }

    public EmailAuth getEmailAuthByToken(String token) {
        return emailAuthMapper.findByToken(token).orElseThrow();
    }

    public void createEmailAuth(CreateEmailAuthDTO dto) {
        emailAuthMapper.create(dto);
    }
}
