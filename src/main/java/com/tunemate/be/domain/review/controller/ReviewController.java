package com.tunemate.be.domain.review.controller;

import com.tunemate.be.domain.review.dto.request.UpdateReviewDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.tag.service.TagService;
import com.tunemate.be.global.annotations.Auth;
import com.tunemate.be.global.annotations.UserID;
import com.tunemate.be.global.responses.OkResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/review")
public class ReviewController {
    private final TagService tagService;
    private final ReviewService reviewService;

    public ReviewController(TagService tagService, ReviewService reviewService) {
        this.tagService = tagService;
        this.reviewService = reviewService;
    }


    @GetMapping("")
    public ResponseEntity<OkResponse<List<ReviewResponseRepositoryDTO>>> getAlbumList(@RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(new OkResponse<>(true, reviewService.findReviewByRecent(limit, offset)));
    }

    @DeleteMapping("/{id}")
    @Auth
    @Operation(security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<OkResponse<Void>> deleteReview(@Parameter(hidden = true) @UserID String userID, @PathVariable Long id) {
        Long parsedUserID = Long.parseLong(userID);
        reviewService.deleteReview(parsedUserID, id);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    @PutMapping("/{id}")
    @Auth
    @Operation(security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<OkResponse<Void>> updateReview(@Parameter(hidden = true) @UserID String userID, @PathVariable Long id, @RequestBody UpdateReviewDTO dto) {
        Long parsedUserID = Long.parseLong(userID);
        reviewService.updateReview(parsedUserID, id, dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    //리뷰태그
    @GetMapping("/tagList")
    public ResponseEntity<List<Tag>> reviewTagList() {
        List<Tag> tagList = tagService.getAllTags();
        return ResponseEntity.ok(tagList);
    }
}
