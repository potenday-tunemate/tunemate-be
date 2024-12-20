package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuth;
import com.tunemate.be.domain.auth.domain.emailAuth.dto.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.emailAuth.dto.UpdateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.emailAuth.repository.EmailAuthRepository;
import com.tunemate.be.domain.email.service.EmailService;
import com.tunemate.be.global.exceptions.CustomException;
import com.tunemate.be.global.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

@Transactional
@Service
public class EmailAuthService {
    private final EmailAuthRepository emailAuthRepository;
    private final EmailService emailService;

    public EmailAuthService(EmailAuthRepository emailAuthRepository, EmailService emailService) {
        this.emailAuthRepository = emailAuthRepository;
        this.emailService = emailService;
    }


    private EmailAuth findEmailAuthOrThrow(Supplier<Optional<EmailAuth>> supplier) {
        return supplier.get().orElseThrow(() -> new CustomException("이메일 인증을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 2002, ""));
    }

    public EmailAuth getEmailAuthByToken(String token) {
        return findEmailAuthOrThrow(() -> emailAuthRepository.findByToken(token));
    }

    public EmailAuth getEmailAuthByEmail(String email) {
        return findEmailAuthOrThrow(() -> emailAuthRepository.findByEmail(email));
    }

    public EmailAuth getEmailAuthById(Long id) {
        return findEmailAuthOrThrow(() -> emailAuthRepository.findById(id));
    }

    public void updateEmailAuth(UpdateEmailAuthDTO dto) {
        EmailAuth emailAuth = getEmailAuthById(dto.getId());
        if (dto.getToken() != null) {
            emailAuth.setToken(dto.getToken());
        }
        if (dto.getExpiredAt() != null) {
            emailAuth.setExpiredAt(Timestamp.from(dto.getExpiredAt()));
        }
        emailAuthRepository.save(emailAuth);
    }

    public void createOrUpdateEmailAuth(CreateEmailAuthDTO dto) {
        String newToken = Utils.getToken().toString();
        Instant expiredAt = Instant.now().plusSeconds(3600);

        EmailAuth emailAuth;

        try {
            emailAuth = getEmailAuthByEmail(dto.getEmail());
            UpdateEmailAuthDTO updateEmailAuthDTO = UpdateEmailAuthDTO.builder()
                    .id(emailAuth.getId())
                    .token(newToken)
                    .expiredAt(expiredAt)
                    .build();
            updateEmailAuth(updateEmailAuthDTO);
        } catch (CustomException error) {
            if (error.getErrorCode() == 2002) {
                emailAuth = EmailAuth.builder()
                        .email(dto.getEmail())
                        .token(newToken)
                        .expiredAt(Timestamp.from(expiredAt))
                        .build();

                try {
                    emailAuthRepository.save(emailAuth);

                    Context context = new Context();
                    context.setVariable("code", newToken);
                    emailService.sendEmail(
                            dto.getEmail(),
                            "[Tunemate] 이메일 인증번호입니다.",
                            "emailAuth",
                            context
                    );
                } catch (Exception e) {
                    throw new CustomException("이메일 인증 생성이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 2003, e.getMessage());
                }
            } else {
                throw error;
            }
        } catch (Exception e) {
            throw new CustomException("이메일 인증 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 2004, e.getMessage());
        }
    }

    public EmailAuth verifyEmailAuth(String token) {
        EmailAuth emailAuth = emailAuthRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("이메일 인증을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 2002, ""));
        if (emailAuth.getExpiredAt().before(Timestamp.from(Instant.now()))) {
            throw new CustomException("이메일 인증이 만료되었습니다.", HttpStatus.UNPROCESSABLE_ENTITY, 2008, "");
        }
        return emailAuth;
    }
}
