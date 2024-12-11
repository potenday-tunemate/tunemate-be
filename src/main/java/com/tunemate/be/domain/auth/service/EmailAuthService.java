package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.domain.EmailAuthMapper;
import com.tunemate.be.domain.email.service.EmailService;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class EmailAuthService {
    private final EmailAuthMapper emailAuthMapper;
    private final EmailService emailService;

    public EmailAuthService(EmailAuthMapper emailAuthMapper, EmailService emailService) {
        this.emailAuthMapper = emailAuthMapper;
        this.emailService = emailService;
    }

    public EmailAuth getEmailAuthByToken(String token) {
        return emailAuthMapper.findByToken(token).orElseThrow(() -> new CustomException("이메일 인증을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 2002));
    }

    public void createEmailAuth(CreateEmailAuthDTO dto) {
        try {
            Context context = new Context();
            context.setVariable("code", "token");
            emailService.sendEmail(dto.email(), "[Tunemate] 이메일 인증번호입니다.", "emailAuth", context);
            emailAuthMapper.create(dto);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("이메일 인증 생성이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 2003);
        }
    }
}
