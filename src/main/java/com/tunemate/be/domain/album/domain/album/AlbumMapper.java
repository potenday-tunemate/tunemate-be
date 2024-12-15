package com.tunemate.be.domain.album.domain.album;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AlbumMapper {
    Optional<Album> findById(@Param("id")Long id);

    void create(Album album);

    List<AlbumGenreDto> albumGenreList();


}
