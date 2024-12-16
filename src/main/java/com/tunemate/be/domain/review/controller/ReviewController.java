package com.tunemate.be.domain.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.tag.service.TagService;
import com.tunemate.be.global.responses.OkResponse;


@RestController
@RequestMapping("/review")
public class ReviewController {
    private final TagService tagService;

    public ReviewController(TagService tagService) {
        this.tagService = tagService;
    }
    

    @GetMapping("")
    public ResponseEntity<OkResponse<Album>> getAlbumById() {
        return ResponseEntity.ok().build();
    }

    //리뷰태그
    @GetMapping("/tagList")
    public ResponseEntity<List<Tag>> reviewTagList() {
        List<Tag> tagList = tagService.getAllTags();
        return ResponseEntity.ok(tagList);
    }
}
