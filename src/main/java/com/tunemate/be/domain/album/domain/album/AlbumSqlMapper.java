package com.tunemate.be.domain.album.domain.album;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlbumSqlMapper {
    public void registAlbumInfoProcess(AlbumDto dto);

    public void registAlbumReviewProcess(AlbumReviewDto dto);

    public AlbumDto albumInfo(@Param("id")int id);

    public List<ReviewTagDto> reviewTagList();

    public void registAlbumReviewTagProcess(AlbumReviewTagDto albumReviewTagDto);
}
