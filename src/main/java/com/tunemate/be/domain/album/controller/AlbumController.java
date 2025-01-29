package com.tunemate.be.domain.album.controller;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.service.GenreService;
import com.tunemate.be.domain.review.dto.request.CreateReviewDTO;
import com.tunemate.be.domain.review.dto.response.AlbumVinylDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import com.tunemate.be.domain.review.service.ReviewService;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.global.annotations.Auth;
import com.tunemate.be.global.annotations.UserID;
import com.tunemate.be.global.responses.OkResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {

    
    private final AlbumService albumService;
    private final ReviewService reviewService;
    private final GenreService genreService;
    public AlbumController(AlbumService albumService, ReviewService reviewService,GenreService genreService) {
        this.albumService = albumService;
        this.reviewService = reviewService;
        this.genreService = genreService;
    } 

    @PostMapping("/backendUse")
    public ResponseEntity<OkResponse<Void>> registAlbum(@RequestBody CreateAlbumDTO dto) {
        albumService.createAlbum(dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OkResponse<Album>> getAlbumById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new OkResponse<>(true, albumService.getAlbumById(id)));
    }

    @GetMapping()
    public ResponseEntity<OkResponse<List<Album>>> getAlbumAll() {
        return ResponseEntity.ok(new OkResponse<>(true, albumService.getAlbumAll()));
    }


    @GetMapping("/{id}/review")
    public ResponseEntity<OkResponse<List<ReviewResponseDTO>>> getAlbumReviews(@PathVariable("id") Long id,
                                                                               @RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(new OkResponse<>(true, reviewService.findAlbumReview(id, limit, offset)));
    }

    @PostMapping("/{id}/review")
    @Auth
    @Operation(security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<OkResponse<Void>> registAlbumReview(@UserID String userID, @PathVariable("id") Long albumID,
                                                              @RequestBody CreateReviewDTO dto) {
        Long parsedUserID = Long.parseLong(userID);
        reviewService.createReview(dto, parsedUserID, albumID);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }

    @GetMapping("/{id}/vinyl")
    public ResponseEntity<OkResponse<List<AlbumVinylDTO>>> getAlbumVinyl(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new OkResponse<>(true, albumService.getAlbumVinyl(id)));
    }

    @GetMapping("/genreList")
    public ResponseEntity<List<Genre>> albumGenreList() {
        List<Genre> genreList = genreService.getAllTags();
        return ResponseEntity.ok(genreList);
    }

    //내가 작성한 리뷰 존재 유무
    @GetMapping("/{id}/existReview")
    @Auth
    @Operation(security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<OkResponse<Boolean>> getExistReview(@UserID String userID,@PathVariable Long id) {
        Long parsedUserID = Long.parseLong(userID);
 
        return ResponseEntity.ok(new OkResponse<Boolean>(true, reviewService.getExistReview(parsedUserID,id)));
    }
}
