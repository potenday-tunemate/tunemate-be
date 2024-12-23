package com.tunemate.be.domain.user.service;

import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.user.domain.user.Follow;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.domain.user.repository.FollowRepository;
import com.tunemate.be.domain.user.domain.user.repository.UserRepository;
import com.tunemate.be.domain.user.dto.CreateUserDTO;
import com.tunemate.be.domain.user.dto.response.ResponseUserDTO;
import com.tunemate.be.global.exceptions.CustomException;
import com.tunemate.be.global.utils.RandomNicknameGenerator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

@Service
@Setter
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Autowired
    private Validator validator;

    public UserService(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
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
            user = User.builder().email(dto.getEmail()).password(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(12)))
                    .nickname(RandomNicknameGenerator.generateNickname()).build();
            userRepository.save(user);
        } catch (Exception ex) {
            throw new CustomException("유저를 생성 할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 3003, "");
        }
    }

    // 추후 기능...
    // public void deleteUser(Long userID, Long id) {
    // User user = userRepository.findById(id)
    // .orElseThrow(() -> new CustomException("유저가 존재하지 않습니다.",
    // HttpStatus.NOT_FOUND, 3005, ""));
    // if (!user.getId().equals(userID)) {
    // throw new CustomException("타인을 삭제하려 시도하고 있습니다.", HttpStatus.UNAUTHORIZED,
    // 3006, "");
    // }
    // userRepository.deleteById(id);
    // }

    public void followUser(Long userId, Long otherUser) {

        try {
            User user = getUserById(userId);
            User userOther = getUserById(otherUser);

            Follow follow = Follow.builder().follower(userOther).following(user).build();
            followRepository.save(follow);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("유저 팔로우에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 3007, e.getMessage());
        }

    }

    public List<ResponseUserDTO> getFollowingList(Long id) {

        return followRepository.findFollowingList(id);
    }

    public List<ResponseUserDTO> getFollowerList(Long id) {

        return followRepository.findFollowerList(id);
    }

    public List<ReviewResponseRepositoryDTO> userWrtieReviewList(Long userId) {
        return userRepository.fingUserReviewList(userId);
    }

}
