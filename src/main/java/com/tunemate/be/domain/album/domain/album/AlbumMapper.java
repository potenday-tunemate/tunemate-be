package com.tunemate.be.domain.album.domain.album;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AlbumMapper {
    Optional<Album> findById(Long id);

    void create(Album album);
}
