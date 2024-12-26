package com.tunemate.be.domain.album.domain.album.repository;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.dto.CreateAlbumDTO;
import com.tunemate.be.domain.search.domain.search.dto.SearchDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a " +
            "JOIN FETCH a.artist ar " +
            "WHERE a.id = :id")
    Optional<Album> findById(@Param("id") Long id);

    @Query("SELECT a FROM Album a " +
       "JOIN a.artist ar " +
       "WHERE a.title LIKE CONCAT('%', :searchWord, '%') " +
       "OR ar.name LIKE CONCAT('%', :searchWord, '%') " +
       "OR CAST(ar.bornYear AS string) LIKE CONCAT('%', :searchWord, '%')")
    Optional<List<CreateAlbumDTO>> findSerachAlbum(@Param("searchWord")String searchWord);
}
