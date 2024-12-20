package com.tunemate.be.domain.album.domain.album.repository;

import com.tunemate.be.domain.album.domain.album.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT album FROM Album album JOIN Artist artist ON album.artist = artist WHERE album.id = :id")
    Optional<Album> findById(@Param("id") Long id);
}
