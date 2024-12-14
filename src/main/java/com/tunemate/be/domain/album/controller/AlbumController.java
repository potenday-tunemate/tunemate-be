package com.tunemate.be.domain.album.controller;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.global.responses.OkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OkResponse<Album>> getAlbumById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new OkResponse<>(true, albumService.getAlbumById(id)));
    }
}
