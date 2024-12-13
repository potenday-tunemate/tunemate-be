package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.domain.EmailAuthMapper;
import com.tunemate.be.domain.auth.domain.UpdateEmailAuthDTO;
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
    private final EmailAuthMapper emailAuthMapper;
    private final EmailService emailService;

    public EmailAuthService(EmailAuthMapper emailAuthMapper, EmailService emailService) {
        this.emailAuthMapper = emailAuthMapper;
        this.emailService = emailService;
    }


    private EmailAuth findEmailAuthOrThrow(Supplier<Optional<EmailAuth>> supplier) {
        return supplier.get().orElseThrow(() -> new CustomException("이메일 인증을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 2002, ""));
    }

    public EmailAuth getEmailAuthByToken(String token) {
        return findEmailAuthOrThrow(() -> emailAuthMapper.findByToken(token));
    }

    public EmailAuth getEmailAuthByEmail(String email) {
        return findEmailAuthOrThrow(() -> emailAuthMapper.findByEmail(email));
    }

    public EmailAuth getEmailAuthById(Long id) {
        return findEmailAuthOrThrow(() -> emailAuthMapper.findById(id));
    }

    public void updateEmailAuth(UpdateEmailAuthDTO dto) {
        EmailAuth emailAuth = getEmailAuthById(dto.getId());

        emailAuthMapper.update(dto);
    }

    public void createOrUpdateEmailAuth(CreateEmailAuthDTO dto) {
        String newToken = Utils.getToken().toString(); // Utils.getToken()이 String을 반환한다고 가정
        Instant expiredAt = Instant.now().plusSeconds(3600);

        try {
            EmailAuth emailAuth = getEmailAuthByEmail(dto.getEmail());

            UpdateEmailAuthDTO updateEmailAuthDTO = UpdateEmailAuthDTO.builder()
                    .id(emailAuth.getId())
                    .token(newToken)
                    .expiredAt(expiredAt)
                    .build();
            emailAuthMapper.update(updateEmailAuthDTO);
        } catch (CustomException error) {
            if (error.getErrorCode() == 2002) {
                dto.setToken(newToken);
                dto.setExpiredAt(expiredAt);
                try {
                    Context context = new Context();
                    context.setVariable("code", dto.getToken());
                    emailAuthMapper.create(dto);
                    emailService.sendEmail(
                            dto.getEmail(),
                            "[Tunemate] 이메일 인증번호입니다.",
                            "emailAuth",
                            context
                    );
                } catch (CustomException e) {
                    throw e;
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
        EmailAuth emailAuth = emailAuthMapper.findByToken(token)
                .orElseThrow(() -> new CustomException("이메일 인증을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 2002, ""));
        if (emailAuth.getExpired_at().before(Timestamp.from(Instant.now()))) {
            throw new CustomException("이메일 인증이 만료되었습니다.", HttpStatus.UNPROCESSABLE_ENTITY, 2008, "");
        }
        return emailAuth;
    }
}
