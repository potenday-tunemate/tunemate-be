package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.emailAuth.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuth;
import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuthMapper;
import com.tunemate.be.domain.auth.domain.emailAuth.UpdateEmailAuthDTO;
import com.tunemate.be.domain.email.service.EmailService;
import com.tunemate.be.global.exceptions.CustomException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailAuthServiceTest {
    @Mock
    private EmailAuthMapper emailAuthMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailAuthService emailAuthService;

    private EmailAuth emailAuth;

    @BeforeEach
    void setUp() {
        emailAuth = EmailAuth.builder()
                .id(1L)
                .email("test@example.com")
                .token("oldToken")
                .expired_at(Timestamp.from(Instant.now().plusSeconds(3600)))
                .build();
    }

    @Test
    void getEmailAuthByToken_존재하면_반환() {
        when(emailAuthMapper.findByToken("token")).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.getEmailAuthByToken("token");
        assertThat(result).isEqualTo(emailAuth);
    }

    @Test
    void getEmailAuthByToken_존재하지않으면_예외발생() {
        when(emailAuthMapper.findByToken("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.getEmailAuthByToken("invalid"))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증을 찾을 수 없습니다.")
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getEmailAuthByEmail_존재하면_반환() {
        when(emailAuthMapper.findByEmail("test@example.com")).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.getEmailAuthByEmail("test@example.com");
        assertThat(result).isEqualTo(emailAuth);
    }

    @Test
    void getEmailAuthByEmail_존재하지않으면_예외발생() {
        when(emailAuthMapper.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.getEmailAuthByEmail("notfound@example.com"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void getEmailAuthById_존재하면_반환() {
        when(emailAuthMapper.findById(1L)).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.getEmailAuthById(1L);
        assertThat(result).isEqualTo(emailAuth);
    }

    @Test
    void getEmailAuthById_존재하지않으면_예외발생() {
        when(emailAuthMapper.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.getEmailAuthById(999L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void updateEmailAuth_정상동작() {
        UpdateEmailAuthDTO dto = UpdateEmailAuthDTO.builder()
                .id(emailAuth.getId())
                .token("newToken")
                .expiredAt(Instant.now().plusSeconds(7200))
                .build();

        when(emailAuthMapper.findById(dto.getId())).thenReturn(Optional.of(emailAuth));

        emailAuthService.updateEmailAuth(dto);

        verify(emailAuthMapper, times(1)).update(dto);
    }

    @Test
    void createOrUpdateEmailAuth_이미존재하는경우_update() throws MessagingException {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("test@example.com");

        when(emailAuthMapper.findByEmail(dto.getEmail())).thenReturn(Optional.of(emailAuth));

        emailAuthService.createOrUpdateEmailAuth(dto);

        // 존재하면 update를 수행
        verify(emailAuthMapper, times(1)).update(any(UpdateEmailAuthDTO.class));
        verify(emailAuthMapper, times(0)).create(dto);
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString(), any(Context.class));
    }

    @Test
    void createOrUpdateEmailAuth_존재하지않는경우_create() throws MessagingException {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("new@example.com");

        // findByEmail이 Optional.empty()를 반환하도록 해서 CustomException(2002번 코드) 발생 상황을 모의
        when(emailAuthMapper.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        emailAuthService.createOrUpdateEmailAuth(dto);

        // 존재하지 않으므로 create 호출, 이메일 서비스도 호출
        verify(emailAuthMapper, times(1)).create(dto);
        verify(emailService, times(1)).sendEmail(eq(dto.getEmail()), eq("[Tunemate] 이메일 인증번호입니다."), eq("emailAuth"), any(Context.class));
    }

    @Test
    void createOrUpdateEmailAuth_생성중_알수없는예외발생시_CustomException() {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("new@example.com");

        when(emailAuthMapper.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        // create 호출 시 예외 발생 시키기
        doThrow(new RuntimeException("DB error")).when(emailAuthMapper).create(dto);

        assertThatThrownBy(() -> emailAuthService.createOrUpdateEmailAuth(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증 생성이 실패했습니다.");
    }

    @Test
    void createOrUpdateEmailAuth_조회중_알수없는예외발생시_CustomException() {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("error@example.com");

        when(emailAuthMapper.findByEmail(dto.getEmail())).thenThrow(new RuntimeException("Unknown error"));

        assertThatThrownBy(() -> emailAuthService.createOrUpdateEmailAuth(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증 처리 중 오류가 발생했습니다.");
    }

    @Test
    void verifyEmailAuth_존재하지않는토큰일때_예외발생() {
        String token = "notExistToken";
        when(emailAuthMapper.findByToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.verifyEmailAuth(token))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증을 찾을 수 없습니다.")
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void verifyEmailAuth_만료된토큰일때_예외발생() {
        emailAuth.setExpired_at(Timestamp.from(Instant.now().minusSeconds(10)));

        when(emailAuthMapper.findByToken("expiredToken")).thenReturn(Optional.of(emailAuth));

        assertThatThrownBy(() -> emailAuthService.verifyEmailAuth("expiredToken"))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증이 만료되었습니다.")
                .extracting("statusCode").isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void verifyEmailAuth_정상토큰일때_반환() {
        when(emailAuthMapper.findByToken("validToken")).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.verifyEmailAuth("validToken");
        assertThat(result).isEqualTo(emailAuth);
    }
}
