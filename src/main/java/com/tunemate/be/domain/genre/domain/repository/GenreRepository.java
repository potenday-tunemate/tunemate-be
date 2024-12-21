package com.tunemate.be.domain.genre.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tunemate.be.domain.genre.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("SELECT genre FROM Genre genre")
    List<Genre> findeAllGenres();

    
}
