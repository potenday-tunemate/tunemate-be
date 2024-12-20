package com.tunemate.be.domain.artist.domain.artist.repository;

import com.tunemate.be.domain.artist.domain.artist.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT DISTINCT artist FROM Artist artist WHERE artist.id = :id")
    Optional<Artist> findById(@Param("id") Long id);
}
