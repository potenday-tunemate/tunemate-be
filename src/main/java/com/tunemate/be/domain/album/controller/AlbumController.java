package com.tunemate.be.domain.album.controller;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumReviewTagDto;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.global.responses.OkResponse;
import com.tunemate.be.domain.review.domain.CreateReviewDTO;
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
    public ResponseEntity<OkResponse<List<Review>>> getAlbumReviews(@PathVariable("id") Long id, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {
        return ResponseEntity.ok(new OkResponse<>(true, reviewService.findAlbumReview(id, limit, offset)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<Void> registAlbumReview(HttpSession session,@PathVariable("id") Long id,@RequestBody Review dto,@RequestBody Map<String, Object> requestBody) {
        User sessionUser = (User) session.getAttribute("loginUser");
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        CreateReviewDTO createReviewDTO = CreateReviewDTO.builder()
                .userID(sessionUser.getId())
                .albumID(id)
                .content(dto.getContent())
                .build();
    
        // Service 호출
        reviewService.createReview(createReviewDTO);

        List<Integer> selectedTagIds = (List<Integer>) requestBody.get("selectedTagIds");
        if (selectedTagIds != null && !selectedTagIds.isEmpty()) {
            selectedTagIds.forEach(tagId -> {
                AlbumReviewTagDto albumReviewTagDto = new AlbumReviewTagDto();
                albumReviewTagDto.setTag_id(tagId);
                albumReviewTagDto.setReview_id(dto.getId().intValue());
                reviewService.registAlbumReviewTag(albumReviewTagDto);
            });
        }

        return ResponseEntity.ok().build();
    }

 


    
}
