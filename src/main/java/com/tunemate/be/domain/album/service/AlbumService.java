package com.tunemate.be.domain.album.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.artist.service.ArtistService;
import com.tunemate.be.domain.genre.service.GenreService;
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

    public AlbumService(AlbumRepository albumRepository, ArtistService artistService,
                        ReviewRepository reviewRepository, GenreService genreService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.reviewRepository = reviewRepository;
        this.genreService = genreService;
    }

    public void createAlbum(CreateAlbumDTO dto) {
        try {
            Album album = new Album();
            album.setTitle(dto.getTitle());
            album.setCoverImg(dto.getCover_img());
            album.setArtist(artistService.getArtistById(dto.getArtist()));
            album.setYear(dto.getYear());
            album.setGenre(genreService.findById(dto.getGenre()));
            album = albumRepository.save(album);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 8001, e.getMessage());
        }
    }

    public List<AlbumVinylDTO> getAlbumVinyl(Long albumId) {
        getAlbumById(albumId);
        return reviewRepository.findAlbumVinylList(albumId);
    }

    public Album getAlbumById(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException("앨범을 찾지 못했습니다.", HttpStatus.NOT_FOUND, 5001, ""));
    }

}
