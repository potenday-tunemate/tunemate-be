package com.tunemate.be.domain.album.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.album.domain.album.AlbumDto;
import com.tunemate.be.domain.album.domain.album.AlbumSqlMapper;

@Service
public class AlbumService {

    @Autowired
    private AlbumSqlMapper albumSqlMapper;

    public void registAlbumInfo(AlbumDto dto) {
        albumSqlMapper.registAlbumInfoProcess(dto);
    }
}
