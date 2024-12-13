package com.tunemate.be.domain.auth.service;


import com.tunemate.be.domain.auth.domain.auth.SigninDTO;
import com.tunemate.be.domain.auth.domain.auth.SigninResponse;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.service.UserService;
import com.tunemate.be.global.exceptions.CustomException;
import com.tunemate.be.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() throws Exception {
        org.springframework.test.util.ReflectionTestUtils.setField(authService, "AccessTokenExpiryTime", 1); // 1 hour
        org.springframework.test.util.ReflectionTestUtils.setField(authService, "RefreshTokenExpiryTime", 2); // 2 hours
    }

    @Test
    void signIn_withValidCredentials_shouldReturnTokens() {
        String email = "test@example.com";
        String rawPassword = "password";
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        User user = new User();
        user.setId(123L);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        SigninDTO dto = SigninDTO.builder()
                .email(email)
                .password(rawPassword)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);

        when(jwtTokenProvider.generateToken(eq(123L), any(Date.class))).thenReturn("dummyToken");

        SigninResponse response = authService.signIn(dto);

        assertNotNull(response);
        assertEquals("dummyToken", response.getAccessToken());
        assertEquals("dummyToken", response.getRefreshToken());

        verify(jwtTokenProvider, times(2)).generateToken(eq(123L), any(Date.class));
    }

    @Test
    void signIn_withInvalidPassword_shouldThrowException() {
        String email = "test@example.com";
        String rawPassword = "password";
        String otherPassword = "other";
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(otherPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        User user = new User();
        user.setId(123L);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        SigninDTO dto = SigninDTO.builder()
                .email(email)
                .password(rawPassword)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);

        CustomException ex = assertThrows(CustomException.class, () -> authService.signIn(dto));
        assertEquals("비밀번호가 유효하지 않습니다.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals(3005, ex.getErrorCode());

        verify(jwtTokenProvider, never()).generateToken(anyLong(), any(Date.class));
    }

    @Test
    void signIn_withNonExistentUser_shouldThrowException() {
        String email = "nonexistent@example.com";
        String rawPassword = "password";

        SigninDTO dto = SigninDTO.builder()
                .email(email)
                .password(rawPassword)
                .build();

        when(userService.getUserByEmail(email)).thenThrow(new CustomException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 3001, ""));

        CustomException ex = assertThrows(CustomException.class, () -> authService.signIn(dto));
        assertEquals("유저를 찾을 수 없습니다.", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals(3001, ex.getErrorCode());

        verify(jwtTokenProvider, never()).generateToken(anyLong(), any(Date.class));
    }

    @Test
    void signIn_tokenProviderThrowsException_shouldThrowCustomException() {
        String email = "test@example.com";
        String rawPassword = "password";
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        User user = new User();
        user.setId(123L);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        SigninDTO dto = SigninDTO.builder()
                .email(email)
                .password(rawPassword)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(jwtTokenProvider.generateToken(eq(123L), any(Date.class))).thenThrow(new RuntimeException("Token generation error"));

        CustomException ex = assertThrows(CustomException.class, () -> authService.signIn(dto));
        assertEquals("토큰 생성이 실패했습니다.", ex.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
        assertEquals(4001, ex.getErrorCode());
    }

    @Test
    void signIn_checkExpiryTime() {
        String email = "test@example.com";
        String rawPassword = "password";
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        User user = new User();
        user.setId(123L);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        SigninDTO dto = SigninDTO.builder()
                .email(email)
                .password(rawPassword)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);

        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        when(jwtTokenProvider.generateToken(eq(123L), dateCaptor.capture())).thenReturn("dummyToken");

        authService.signIn(dto);

        assertEquals(2, dateCaptor.getAllValues().size());

        Date accessTokenDate = dateCaptor.getAllValues().get(0);
        Date refreshTokenDate = dateCaptor.getAllValues().get(1);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, 1);
        Date expectedAccessDate = cal.getTime();

        long diffAccess = Math.abs(accessTokenDate.getTime() - expectedAccessDate.getTime());
        assertTrue(diffAccess < 5000, "Access Token 시간차가 5초 이내인지 확인");

        cal.setTime(now);
        cal.add(Calendar.HOUR, 2);
        Date expectedRefreshDate = cal.getTime();
        long diffRefresh = Math.abs(refreshTokenDate.getTime() - expectedRefreshDate.getTime());
        assertTrue(diffRefresh < 5000, "Refresh Token 시간차가 5초 이내인지 확인");
    }
}
