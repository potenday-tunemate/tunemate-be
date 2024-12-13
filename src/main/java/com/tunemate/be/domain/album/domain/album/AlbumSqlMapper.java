package com.tunemate.be.domain.album.domain.album;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlbumSqlMapper {
    public void registAlbumInfoProcess(AlbumDto dto);
}
