package com.tunemate.be.domain.genre.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.AlbumGenreDto;
import com.tunemate.be.domain.album.domain.album.repository.AlbumGenre;
import com.tunemate.be.domain.album.domain.album.repository.AlbumGenreRepository;
import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.domain.repository.GenreRepository;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.global.exceptions.CustomException;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllTags() {
        return genreRepository.findeAllGenres();
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new CustomException("장르를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, 10001, ""));
    }

    public List<Map<String, Object>> getGenresBySortType(Long genreId,String sortType) {
        

        if ("new".equalsIgnoreCase(sortType)) {
            List<Object[]> results = genreRepository.findAllGenresByNew(genreId);
        
        return results.stream().map(row -> {
            Album album = (Album) row[0];
            String artistName = (String) row[1];
            Map<String, Object> map = new HashMap<>();
            map.put("albumGenre", album);
            map.put("artistName", artistName);
            return map;
        }).collect(Collectors.toList());
        } 
        // 추후에 할꺼임
        // else if ("popular".equalsIgnoreCase(sortType)) {
        //     return genreRepository.findAllGenresByPopular();
        // } 
        else {
            throw new CustomException("앨범 정렬에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 7001, "");
        }


    }
}


