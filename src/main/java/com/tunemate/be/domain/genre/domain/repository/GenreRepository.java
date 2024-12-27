package com.tunemate.be.domain.genre.domain.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.genre.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("SELECT genre FROM Genre genre")
    List<Genre> findeAllGenres();

    @Query("SELECT a, ar.name,a.id, g.genre FROM Album a " +
            "JOIN a.genre g " +
            "JOIN a.artist ar " +
            "WHERE g.id = :genreId " +
            "ORDER BY a.createdAt DESC")
    List<Object[]> findAllGenresByNew(@Param("genreId") Long genreId);

    @Query("SELECT a, ar.name, COUNT(r) AS reviewCount ,a.id, g.genre " +
            "FROM Review r " +
            "JOIN r.album a " +
            "JOIN a.genre g " +
            "JOIN a.artist ar " +
            "WHERE g.id = :genreId " +
            "GROUP BY a.id " +
            "ORDER BY reviewCount DESC")
    List<Object[]> findAllGenresByPopular(@Param("genreId") Long genreId);

}
