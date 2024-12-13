package com.tunemate.be.domain.user.service;


import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.domain.user.UserMapper;
import com.tunemate.be.global.exceptions.CustomException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        userService.setValidator(validator);
    }

    @Test
    void createUser_withValidData_shouldCreateUser() {
        CreateUserDTO dto = CreateUserDTO.builder()
                .email("valid@example.com")
                .password("password")
                .nickname(null)
                .build();

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        when(userMapper.findByEmail("valid@example.com")).thenReturn(Optional.empty());

        doNothing().when(userMapper).create(dto);

        assertDoesNotThrow(() -> userService.createUser(dto));

        // then
        // userMapper.create가 호출되었는지 확인
        verify(userMapper, times(1)).create(any(CreateUserDTO.class));
    }

    @Test
    void createUser_withInvalidEmail_shouldThrowException() {
        // given
        CreateUserDTO dto = CreateUserDTO.builder()
                .email("invalid-email")
                .password("password")
                .build();

        ConstraintViolation violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("유효한 이메일 형식이 아닙니다.");
        when(validator.validate(dto)).thenReturn(Set.of(violation));

        CustomException ex = assertThrows(CustomException.class, () -> userService.createUser(dto));
        assertEquals("입력값 검증 실패: 유효한 이메일 형식이 아닙니다.", ex.getMessage());
        assertEquals(3004, ex.getErrorCode());
    }

    @Test
    void createUser_withExistingEmail_shouldThrowException() {
        CreateUserDTO dto = CreateUserDTO.builder()
                .email("exists@example.com")
                .password("password")
                .build();

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        User existingUser = new User(); // 가상의 User 객체
        when(userMapper.findByEmail("exists@example.com")).thenReturn(Optional.of(existingUser));

        CustomException ex = assertThrows(CustomException.class, () -> userService.createUser(dto));
        assertEquals("유저 이메일이 이미 존재합니다.", ex.getMessage());
        assertEquals(3002, ex.getErrorCode());
    }

    @Test
    void createUser_unexpectedErrorDuringCreate_shouldThrowException() {
        CreateUserDTO dto = CreateUserDTO.builder()
                .email("valid@example.com")
                .password("password")
                .build();

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        when(userMapper.findByEmail("valid@example.com")).thenReturn(Optional.empty());

        doThrow(new RuntimeException("DB error")).when(userMapper).create(any(CreateUserDTO.class));

        CustomException ex = assertThrows(CustomException.class, () -> userService.createUser(dto));
        assertEquals("유저를 생성 할 수 없습니다.", ex.getMessage());
        assertEquals(3003, ex.getErrorCode());
    }
}
