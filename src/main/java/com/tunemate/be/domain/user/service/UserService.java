package com.tunemate.be.domain.user.service;

import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.domain.user.repository.UserRepository;
import com.tunemate.be.domain.user.dto.CreateUserDTO;
import com.tunemate.be.global.exceptions.CustomException;
import com.tunemate.be.global.utils.RandomNicknameGenerator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
@Setter
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private Validator validator;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserOrThrow(Supplier<Optional<User>> supplier) {
        return supplier.get().orElseThrow(() -> new CustomException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 3001, ""));
    }

    public User getUserByEmail(String email) {
        return findUserOrThrow(() -> userRepository.findByEmail(email));
    }

    public User getUserById(Long id) {
        return findUserOrThrow(() -> userRepository.findById(id));
    }

    public void createUser(CreateUserDTO dto) {
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMsg = violations.iterator().next().getMessage();
            throw new CustomException("입력값 검증 실패: " + errorMsg, HttpStatus.BAD_REQUEST, 3004, "");
        }
        User user = null;
        try {
            user = getUserByEmail(dto.getEmail());
        } catch (CustomException ignored) {
        }

        if (user != null) {
            throw new CustomException("유저 이메일이 이미 존재합니다.", HttpStatus.UNPROCESSABLE_ENTITY, 3002, "");
        }
        try {
            user = User.builder().email(dto.getEmail()).password(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(12))).nickname(RandomNicknameGenerator.generateNickname()).build();
            userRepository.save(user);
        } catch (Exception ex) {
            throw new CustomException("유저를 생성 할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 3003, "");
        }
    }
}
