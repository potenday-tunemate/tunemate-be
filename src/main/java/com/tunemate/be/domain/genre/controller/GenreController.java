package com.tunemate.be.domain.genre.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.service.GenreService;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.global.responses.OkResponse;

@RestController
@RequestMapping("/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("")
    public ResponseEntity<OkResponse<List<Genre>>> getAlbumList() {
        return ResponseEntity.ok(new OkResponse<>(true, genreService.getAllTags()));
    }
    
    @GetMapping("/{id}/{sortType}")
    public ResponseEntity<List<Map<String, Object>>> getGenres(
            @PathVariable Long id,
            @PathVariable String sortType) {
        
                List<Map<String, Object>> genres = genreService.getGenresBySortType(id,sortType);
        return ResponseEntity.ok(genres);
    }
}
