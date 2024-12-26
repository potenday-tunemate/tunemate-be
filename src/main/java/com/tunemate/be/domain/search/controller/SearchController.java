package com.tunemate.be.domain.search.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.search.domain.search.dto.SearchDTO;
import com.tunemate.be.domain.search.service.SearchService;
import com.tunemate.be.global.responses.OkResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping("")
    public ResponseEntity<OkResponse<List<Album>>> getSerchAlbum(@RequestParam String searchWord) {
        List<Album> searchAlbum = searchService.findSearchAlbum(searchWord);
        return ResponseEntity.ok(new OkResponse<>(true, searchAlbum));
    }
    

}
