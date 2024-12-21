package com.tunemate.be.domain.album.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.service.GenreService;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.domain.repository.ReviewRepository;
import com.tunemate.be.domain.review.dto.response.AlbumVinylDTO;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private final ReviewRepository reviewRepository;
    private final GenreService genreService;

    public AlbumService(AlbumRepository albumRepository, ArtistService artistService, 
                        ReviewRepository reviewRepository,GenreService genreService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.reviewRepository = reviewRepository;
        this.genreService = genreService;
    }

    public void createAlbum(CreateAlbumDTO dto) {
        Artist artist = artistService.getArtistById(dto.getArtist());
        Album album = Album.builder().title(dto.getTitle()).
                year(dto.getYear()).
                coverImg(dto.getCover_img()).
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

    public Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new CustomException("앨범을 찾지 못했습니다.", HttpStatus.NOT_FOUND, 5001, ""));
    }

    public void createReview(CreateAlbumDTO dto) {
        try {
            List<Genre> genres = new ArrayList<>();
            if (dto.getSelected_genres() != null && !dto.getSelected_genres().isEmpty()) {
                genres = dto.getSelected_genres().stream().map((id) -> genreService.findById(Long.valueOf(id))).toList();
            }
            Album album = Album.builder()
            .title(dto.getTitle())
            .coverImg(dto.getCover_img())
            .artist(artistService.getArtistById(dto.getArtist()))
            .year(dto.getYear())
            .genre(genres)
            .build();
        albumRepository.save(album);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 8001, e.getMessage());
        }
    }
}
