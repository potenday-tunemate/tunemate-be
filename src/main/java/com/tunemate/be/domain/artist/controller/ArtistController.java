package com.tunemate.be.domain.artist.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.domain.artist.dto.CreateArtistDTO;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.global.responses.OkResponse;

@RestController
@RequestMapping("/artist")
public class ArtistController {
    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);
    private ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping("")
    public ResponseEntity<OkResponse<Void>> registArtist(@RequestBody CreateArtistDTO dto) {
        System.out.println("확인artist !" + dto.getBorn_year());

        artistService.createArtist(dto);
        return ResponseEntity.ok(new OkResponse<>(true, null));
    }
}
