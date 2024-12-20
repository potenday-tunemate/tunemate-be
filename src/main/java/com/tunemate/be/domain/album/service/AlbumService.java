package com.tunemate.be.domain.album.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;

    public AlbumService(AlbumRepository albumRepository, ArtistService artistService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
    }

    public void createAlbum(CreateAlbumDTO dto) {
        Artist artist = artistService.getArtistById(dto.getArtist());
        Album album = Album.builder().title(dto.getTitle()).
                year(dto.getYear()).
                coverImg(dto.getCoverImg()).
                artist(artist).build();
        try {
            albumRepository.save(album);
        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 5002, "");
        }
    }

    public Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new CustomException("앨범을 찾지 못했습니다.", HttpStatus.NOT_FOUND, 5001, ""));
    }
}
