package com.tunemate.be.domain.album.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.AlbumInfoDto;
import com.tunemate.be.domain.album.service.AlbumService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("album/")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @PostMapping("registAlbumInfo")
    public ResponseEntity<Void> registAlbumInfo(AlbumInfoDto dto){
        albumService.registAlbumInfo(dto);
        return ResponseEntity.ok().build();
    }

}
