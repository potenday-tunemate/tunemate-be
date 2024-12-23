package com.tunemate.be.domain.genre.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumGenre;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.service.GenreService;

@RestController
@RequestMapping("/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}/{sortType}")
    public ResponseEntity<List<Map<String, Object>>> getGenres(
            @PathVariable Long id,
            @PathVariable String sortType) {
        
                List<Map<String, Object>> genres = genreService.getGenresBySortType(id,sortType);
        return ResponseEntity.ok(genres);
    }
}
