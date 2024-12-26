package com.tunemate.be.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.dto.response.ResponseUserDTO;
import com.tunemate.be.domain.user.service.UserService;
import com.tunemate.be.global.annotations.Auth;
import com.tunemate.be.global.annotations.UserID;
import com.tunemate.be.global.responses.OkResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @Auth
    public ResponseEntity<User> getUserProfile(@UserID String userId) {
        Long parsedUserID = Long.parseLong(userId);
        User user = userService.getUserById(parsedUserID);
        return ResponseEntity.ok(user);
    }

    // 추후기능
    // @DeleteMapping("/{id}/follow")
    // @Auth
    // public ResponseEntity<OkResponse<Void>> deleteReview(@UserID String userID,
    // @PathVariable Long id) {
    // Long parsedUserID = Long.parseLong(userID);
    // userService.deleteUser(parsedUserID, id);
    // return ResponseEntity.ok(new OkResponse<>(true, null));
    // }

    @PostMapping("/{id}/follow")
    @Auth
    public ResponseEntity<OkResponse<Void>> registAlbum(@UserID String userID, @PathVariable("id") Long otherUser) {
        Long userId = Long.parseLong(userID);
        userService.followUser(userId, otherUser);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    // 내가 팔로윙하는사람 목록
    @GetMapping("/{id}/following")
    public ResponseEntity<List<ResponseUserDTO>> getFollowingList(@PathVariable("id") Long id) {
        List<ResponseUserDTO> followingList = userService.getFollowingList(id);
        return ResponseEntity.ok(followingList);
    }

    @GetMapping("/{id}/follower")
    public ResponseEntity<List<ResponseUserDTO>> getFollowerList(@PathVariable("id") Long id) {
        List<ResponseUserDTO> followerList = userService.getFollowerList(id);
        return ResponseEntity.ok(followerList);
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<List<ReviewResponseRepositoryDTO>> userWrtieReviewList(@PathVariable("id") Long userId) {
        List<ReviewResponseRepositoryDTO> userReviewList = userService.userWrtieReviewList(userId);
        return ResponseEntity.ok(userReviewList);
    }

}
