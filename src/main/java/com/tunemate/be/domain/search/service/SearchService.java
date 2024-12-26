package com.tunemate.be.domain.search.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.album.domain.album.repository.AlbumRepository;
import com.tunemate.be.domain.search.domain.search.dto.SearchDTO;
import com.tunemate.be.global.exceptions.CustomException;

@Service
public class SearchService {

    private final AlbumRepository albumRepository;

    public SearchService(AlbumRepository albumRepository){
        this.albumRepository = albumRepository;
    }

    public List<CreateAlbumDTO> findSearchClub(SearchDTO searchDTO) {
        String searchWord = searchDTO.getSearchWord();
        return albumRepository.findSerachAlbum(searchWord).orElseThrow(() -> new CustomException("앨범을 찾지 못했습니다.", HttpStatus.NOT_FOUND, 10001, ""));
    }

   
}
