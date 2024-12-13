package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.auth.SigninDTO;
import com.tunemate.be.domain.auth.domain.auth.SigninResponse;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.service.UserService;
import com.tunemate.be.global.exceptions.CustomException;
import com.tunemate.be.global.jwt.JwtTokenProvider;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access_token.time}")
    private int AccessTokenExpiryTime;

    @Value("${jwt.refresh_token.time}")
    private int RefreshTokenExpiryTime;

    public AuthService(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public SigninResponse signIn(SigninDTO dto) {
        try {
            User user = userService.getUserByEmail(dto.getEmail());
            Date date = new Date();
            Calendar accessTokenCal = Calendar.getInstance();
            Calendar refreshTokenCal = Calendar.getInstance();
            accessTokenCal.setTime(date);
            refreshTokenCal.setTime(date);
            boolean isValid = BCrypt.checkpw(dto.getPassword(), user.getPassword());
            if (!isValid) {
                throw new CustomException("비밀번호가 유효하지 않습니다.", HttpStatus.BAD_REQUEST, 3005, "");
            }
            accessTokenCal.add(Calendar.HOUR, AccessTokenExpiryTime);
            refreshTokenCal.add(Calendar.HOUR, RefreshTokenExpiryTime);
            String accessToken = jwtTokenProvider.generateToken(user.getId(), accessTokenCal.getTime());
            String refreshToken = jwtTokenProvider.generateToken(user.getId(), refreshTokenCal.getTime());
            return SigninResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("토큰 생성이 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY, 4001, e.getMessage());
        }
    }

    public void signUp(CreateUserDTO dto) {
        userService.createUser(dto);
    }
}
