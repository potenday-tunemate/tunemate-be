package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuth;
import com.tunemate.be.domain.auth.domain.emailAuth.dto.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.emailAuth.dto.UpdateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.emailAuth.repository.EmailAuthRepository;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailAuthServiceTest {
    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailAuthService emailAuthService;

    private EmailAuth emailAuth;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("jwt.secret", () -> "thisIsASecretKeyThatShouldBeAtLeast32BytesLongOkWhynotWork");
        registry.add("jwt.access_token.time", () -> 24);
        registry.add("jwt.refresh_token.time", () -> 48);
    }

    @BeforeEach
    void setUp() {
        emailAuth = EmailAuth.builder()
                .id(1L)
                .email("test@example.com")
                .token("oldToken")
                .expiredAt(Timestamp.from(Instant.now().plusSeconds(3600)))
                .build();
    }

    @Test
    void getEmailAuthByToken_존재하면_반환() {
        when(emailAuthRepository.findByToken("token")).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.getEmailAuthByToken("token");
        assertThat(result).isEqualTo(emailAuth);
    }

    @Test
    void getEmailAuthByToken_존재하지않으면_예외발생() {
        when(emailAuthRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.getEmailAuthByToken("invalid"))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증을 찾을 수 없습니다.")
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getEmailAuthByEmail_존재하면_반환() {
        when(emailAuthRepository.findByEmail("test@example.com")).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.getEmailAuthByEmail("test@example.com");
        assertThat(result).isEqualTo(emailAuth);
    }

    @Test
    void getEmailAuthByEmail_존재하지않으면_예외발생() {
        when(emailAuthRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.getEmailAuthByEmail("notfound@example.com"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void getEmailAuthById_존재하면_반환() {
        when(emailAuthRepository.findById(1L)).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.getEmailAuthById(1L);
        assertThat(result).isEqualTo(emailAuth);
    }

    @Test
    void getEmailAuthById_존재하지않으면_예외발생() {
        when(emailAuthRepository.findById(999L)).thenReturn(Optional.empty());

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

        EmailAuth emailAuth = EmailAuth.builder().token(dto.getToken()).expiredAt(Timestamp.from(dto.getExpiredAt())).build();
        when(emailAuthRepository.findById(dto.getId())).thenReturn(Optional.of(emailAuth));

        emailAuthService.updateEmailAuth(dto);

        verify(emailAuthRepository, times(1)).save(emailAuth);
    }

    @Test
    void createOrUpdateEmailAuth_존재하지않는경우_create() throws MessagingException {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("new@example.com");

        EmailAuth emailAuth = EmailAuth.builder().email(dto.getEmail()).build();
        when(emailAuthRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        emailAuthService.createOrUpdateEmailAuth(dto);

        verify(emailAuthRepository, times(1)).findByEmail(dto.getEmail());
    }

    @Test
    void createOrUpdateEmailAuth_생성중_알수없는예외발생시_CustomException() {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("new@example.com");

        EmailAuth emailAuth = EmailAuth.builder().email(dto.getEmail()).build();
        when(emailAuthRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        // create 호출 시 예외 발생 시키기
        doThrow(new RuntimeException("DB error")).when(emailAuthRepository).save(emailAuth);

        assertThatThrownBy(() -> emailAuthService.createOrUpdateEmailAuth(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증 생성이 실패했습니다.");
    }

    @Test
    void createOrUpdateEmailAuth_조회중_알수없는예외발생시_CustomException() {
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO();
        dto.setEmail("error@example.com");

        when(emailAuthRepository.findByEmail(dto.getEmail())).thenThrow(new RuntimeException("Unknown error"));

        assertThatThrownBy(() -> emailAuthService.createOrUpdateEmailAuth(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증 처리 중 오류가 발생했습니다.");
    }

    @Test
    void verifyEmailAuth_존재하지않는토큰일때_예외발생() {
        String token = "notExistToken";
        when(emailAuthRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailAuthService.verifyEmailAuth(token))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증을 찾을 수 없습니다.")
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void verifyEmailAuth_만료된토큰일때_예외발생() {
        emailAuth.setExpiredAt(Timestamp.from(Instant.now().minusSeconds(10)));

        when(emailAuthRepository.findByToken("expiredToken")).thenReturn(Optional.of(emailAuth));

        assertThatThrownBy(() -> emailAuthService.verifyEmailAuth("expiredToken"))
                .isInstanceOf(CustomException.class)
                .hasMessage("이메일 인증이 만료되었습니다.")
                .extracting("statusCode").isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void verifyEmailAuth_정상토큰일때_반환() {
        when(emailAuthRepository.findByToken("validToken")).thenReturn(Optional.of(emailAuth));

        EmailAuth result = emailAuthService.verifyEmailAuth("validToken");
        assertThat(result).isEqualTo(emailAuth);
    }
}
