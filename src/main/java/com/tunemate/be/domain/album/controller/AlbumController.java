package com.tunemate.be.domain.album.controller;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumReviewRequest;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.global.responses.OkResponse;
import com.tunemate.be.domain.review.domain.CreateReviewDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;
    private final ReviewService reviewService;

    public AlbumController(AlbumService albumService, ReviewService reviewService) {
        this.albumService = albumService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OkResponse<Album>> getAlbumById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new OkResponse<>(true, albumService.getAlbumById(id)));
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<OkResponse<List<Review>>> getAlbumReviews(@PathVariable("id") Long id,
            @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {
        return ResponseEntity.ok(new OkResponse<>(true, reviewService.findAlbumReview(id, limit, offset)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<Void> registAlbumReview(HttpServletRequest request, @PathVariable("id") Long id,
            @RequestBody AlbumReviewRequest albumReviewRequest) {
        String authenticatedUserID = (String) request.getAttribute("authenticatedUserID");
        if (authenticatedUserID == null) {
            authenticatedUserID = "123";
        }


        List<Integer> selectedTags = albumReviewRequest.getSelectedTags();

        reviewService.createReview(albumReviewRequest.getCreateReviewDTO(), selectedTags);

        return ResponseEntity.ok().build();
    }

}
