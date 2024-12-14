package com.tunemate.be.domain.album.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.album.domain.album.AlbumDto;
import com.tunemate.be.domain.album.domain.album.AlbumReviewDto;
import com.tunemate.be.domain.album.domain.album.*;

import com.tunemate.be.domain.album.domain.album.AlbumSqlMapper;

@Service
public class AlbumService {

    @Autowired
    private AlbumSqlMapper albumSqlMapper;

    public void registAlbumInfo(AlbumDto dto) {
        albumSqlMapper.registAlbumInfoProcess(dto);
    }

    public void registAlbumReview(AlbumReviewDto dto) {
        albumSqlMapper.registAlbumReviewProcess(dto);

    }

    public AlbumDto albumDetailInfo(int id){
        return albumSqlMapper.albumInfo(id);
    }

    public List<ReviewTagDto> reviewTagList(){
        return albumSqlMapper.reviewTagList();
    }

    public void registAlbumReviewTag(AlbumReviewTagDto albumReviewTagDto){
        albumSqlMapper.registAlbumReviewTagProcess(albumReviewTagDto);
    }
    
}
