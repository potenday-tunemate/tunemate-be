package com.tunemate.be.domain.album.domain;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlbumSqlMapper {
    public void registAlbumInfoProcess(AlbumInfoDto albumInfoDto);
}
