package com.tunemate.be.domain.artist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.artist.domain.artist.ArtistDto;
import com.tunemate.be.domain.artist.domain.artist.ArtistSqlMapper;

@Service
public class ArtistService {
    @Autowired
    private ArtistSqlMapper artistSqlMapper;

    public void registArtistInfo(ArtistDto dto) {
        artistSqlMapper.registArtistInfoProcess(dto);
    }
}
