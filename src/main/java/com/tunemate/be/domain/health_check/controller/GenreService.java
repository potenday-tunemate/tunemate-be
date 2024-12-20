package com.tunemate.be.domain.health_check.controller;

import com.tunemate.be.domain.genre.domain.Genre;
import com.tunemate.be.domain.genre.domain.repository.GenreRepository;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre findByName(String name) {
        return genreRepository.findByName(name).orElseThrow(() -> new CustomException("해당 이름에 일치하는 장르가 없습니다.", HttpStatus.NOT_FOUND, 12001, ""));
    }
}
