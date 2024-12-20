package com.tunemate.be.domain.album.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.domain.repository.GenreRepository;
import com.tunemate.be.domain.health_check.controller.GenreService;
import com.tunemate.be.domain.review.domain.repository.ReviewRepository;
import com.tunemate.be.domain.review.dto.response.AlbumVinylDTO;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private final ReviewRepository reviewRepository;
    private final GenreService genreService;

    public AlbumService(AlbumRepository albumRepository, ArtistService artistService, ReviewRepository reviewRepository, GenreRepository genreRepository, GenreService genreService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.reviewRepository = reviewRepository;
        this.genreService = genreService;
    }

    public void createAlbum(CreateAlbumDTO dto) {
        Artist artist = artistService.getArtistById(dto.getArtist());
        List<Genre> genres = dto.getGenres().stream().map(genreService::findByName).toList();
        Album album = Album.builder().title(dto.getTitle()).
                year(dto.getYear()).
                coverImg(dto.getCoverImg()).
                genres(genres).
                artist(artist).build();
        try {
            albumRepository.save(album);
        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 5002, "");
        }
    }

    public List<AlbumVinylDTO> getAlbumVinyl(Long albumId) {
        getAlbumById(albumId);
        return reviewRepository.findAlbumVinylList(albumId);
    }

    public List<Album> getAlbumByGenreInRecent(String genreName) {
        genreService.findByName(genreName);
        return albumRepository.findByGenreInRecent(genreName);
    }

    public Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new CustomException("앨범을 찾지 못했습니다.", HttpStatus.NOT_FOUND, 5001, ""));
    }
}
