package com.tunemate.be.domain.genre.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.genre.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("SELECT genre FROM Genre genre")
    List<Genre> findeAllGenres();

    @Query("SELECT a, ar.name FROM AlbumGenre ag " +
    "JOIN ag.album a " +
    "JOIN a.artist ar " +
    "WHERE ag.genre.id = :genreId " +
    "ORDER BY ag.createdAt DESC")   
    List<Object[]> findAllGenresByNew(@Param("genreId") Long genreId);

    // List<Genre> findAllGenresByPopular();

    
}
