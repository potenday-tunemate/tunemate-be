package com.tunemate.be.domain.album.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.*;

import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("album/")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @PostMapping("registAlbumInfo")
    public ResponseEntity<Void> registAlbumInfo(@RequestBody AlbumDto dto,HttpSession session) {
        CreateUserDTO sessionUser = (CreateUserDTO)session.getAttribute("loginUser");
        dto.setId(sessionUser.getId());

        albumService.registAlbumInfo(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("registAlbumReview")
    public ResponseEntity<Void> registAlbumReview(HttpSession session,@RequestParam("id")int id,@RequestBody AlbumReviewDto dto, @RequestParam List<Integer> selectedTagIds) {
        CreateUserDTO sessionUser = (CreateUserDTO)session.getAttribute("loginUser");
        dto.setUser_id(sessionUser.getId());
        dto.setAlbum_id(id);

        albumService.registAlbumReview(dto);

        selectedTagIds.forEach(tagId -> {
            System.out.println("Selected Tag ID: " + tagId);
            AlbumReviewTagDto albumReviewTagDto = new AlbumReviewTagDto();
            albumReviewTagDto.setTag_id(tagId);
            int reviewId = dto.getId();
            albumReviewTagDto.setReview_id(reviewId);
            albumService.registAlbumReviewTag(albumReviewTagDto);
        });
        
        return ResponseEntity.ok().build();
    }

    //특정앨범 정보
    @GetMapping("info")
    public ResponseEntity<AlbumDto> albumListAll(@RequestParam("id")int id) {
        AlbumDto album = albumService.albumDetailInfo(id);
        return ResponseEntity.ok(album);
    }

    //리뷰태그
    @GetMapping("tagList")
    public ResponseEntity<List<ReviewTagDto>> reviewTagList() {
        List<ReviewTagDto> tagList = albumService.reviewTagList();
        return ResponseEntity.ok(tagList);
    }

    




}
