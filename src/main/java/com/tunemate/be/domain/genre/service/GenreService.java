package com.tunemate.be.domain.genre.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
}
