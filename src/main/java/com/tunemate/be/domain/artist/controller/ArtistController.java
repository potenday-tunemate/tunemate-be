package com.tunemate.be.domain.artist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.artist.domain.artist.ArtistDto;
import com.tunemate.be.domain.artist.service.ArtistService;

@RestController
@RequestMapping("artist/")
public class ArtistController {
    @Autowired
    private ArtistService artistService;

    @PostMapping("registArtistInfo")
    public ResponseEntity<Void> registAlbumInfo(@RequestBody ArtistDto dto) {
        System.out.println(dto);
        artistService.registArtistInfo(dto);
        return ResponseEntity.ok().build();
    }
}
