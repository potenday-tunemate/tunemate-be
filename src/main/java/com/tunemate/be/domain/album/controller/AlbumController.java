package com.tunemate.be.domain.album.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.AlbumDto;
import com.tunemate.be.domain.album.domain.album.AlbumReviewDto;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<Void> registAlbumReview(HttpSession session,@RequestParam("id")int id,@RequestBody AlbumReviewDto dto) {
        CreateUserDTO sessionUser = (CreateUserDTO)session.getAttribute("loginUser");
        dto.setUser_id(sessionUser.getId());
        dto.setAlbum_id(id);

        albumService.registAlbumReview(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("info")
    public ResponseEntity<AlbumDto> albumListAll(@RequestParam("id")int id) {
        AlbumDto album = albumService.albumDetailInfo(id);
        return ResponseEntity.ok(album);
    }




}
