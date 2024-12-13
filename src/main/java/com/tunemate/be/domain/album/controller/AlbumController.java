package com.tunemate.be.domain.album.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.AlbumDto;
import com.tunemate.be.domain.album.service.AlbumService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("album/")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @PostMapping("registAlbumInfo")
    public ResponseEntity<Void> registAlbumInfo(@RequestBody AlbumDto dto) {
        
        albumService.registAlbumInfo(dto);
        return ResponseEntity.ok().build();
    }

}
