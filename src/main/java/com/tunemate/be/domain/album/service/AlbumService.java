package com.tunemate.be.domain.album.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.album.domain.AlbumInfoDto;
import com.tunemate.be.domain.album.domain.AlbumSqlMapper;

@Service
public class AlbumService {

    @Autowired
    private AlbumSqlMapper albumSqlMapper;

    public void registAlbumInfo(AlbumInfoDto dto){
        albumSqlMapper.registAlbumInfoProcess(dto);
    }
}
