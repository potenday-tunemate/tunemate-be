package com.tunemate.be.domain.artist.service;

import com.tunemate.be.domain.artist.domain.artist.Artist;
import com.tunemate.be.domain.artist.domain.artist.repository.ArtistRepository;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Artist getArtistById(Long id) {
        return artistRepository.findById(id).orElseThrow(() -> new CustomException("아티스트를 찾지 못했습니다.", HttpStatus.NOT_FOUND, 6001, ""));
    }

    public void createArtist(Artist artist) {
        try {
            artistRepository.save(artist);
        } catch (Exception e) {
            throw new CustomException("아티스트 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 6002, "");
        }
    }
}
