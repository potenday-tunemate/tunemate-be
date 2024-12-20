package com.tunemate.be.domain.album.controller;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.review.domain.CreateReviewDTO;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.global.annotations.UserID;
import com.tunemate.be.global.responses.OkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                                                                    @RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(new OkResponse<>(true, reviewService.findAlbumReview(id, limit, offset)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<OkResponse<Void>> registAlbumReview(@UserID String ID, @PathVariable("id") Long id,
                                                              @RequestBody CreateReviewDTO dto) {
        reviewService.createReview(dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

}
