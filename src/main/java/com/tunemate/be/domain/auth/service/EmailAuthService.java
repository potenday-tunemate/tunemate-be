package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.domain.EmailAuthMapper;
import com.tunemate.be.domain.email.service.EmailService;
import com.tunemate.be.global.exceptions.CustomException;
import com.tunemate.be.global.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.Instant;

@Service
public class EmailAuthService {
    private final EmailAuthMapper emailAuthMapper;
    private final EmailService emailService;

    public EmailAuthService(EmailAuthMapper emailAuthMapper, EmailService emailService) {
        this.emailAuthMapper = emailAuthMapper;
        this.emailService = emailService;
    }

    public EmailAuth getEmailAuthByToken(String token) {
        return emailAuthMapper.findByToken(token).orElseThrow(() -> new CustomException("이메일 인증을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 2002, ""));
    }

    @Async
    public void createEmailAuth(CreateEmailAuthDTO dto) {
        dto.setToken(Utils.getToken().toString());
        dto.setExpiredAt(Instant.now().plusSeconds(3600));
        try {
            Context context = new Context();
            context.setVariable("code", dto.getToken());
            emailAuthMapper.create(dto);
            emailService.sendEmail(dto.getEmail(), "[Tunemate] 이메일 인증번호입니다.", "emailAuth", context);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException("이메일 인증 생성이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 2003, e.getMessage());
        }
    }
}
