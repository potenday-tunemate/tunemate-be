package com.tunemate.be.domain.artist.domain.artist;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ArtistMapper {
    Optional<Artist> findById(Long id);

    void create(Artist artist);
}
