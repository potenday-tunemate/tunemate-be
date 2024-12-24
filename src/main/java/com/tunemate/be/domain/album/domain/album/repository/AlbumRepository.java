package com.tunemate.be.domain.album.domain.album.repository;

import com.tunemate.be.domain.album.domain.album.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a " +
            "JOIN  a.artist ar " +
            "WHERE a.id = :id")
    Optional<Album> findById(@Param("id") Long id);
}
