package com.tunemate.be.domain.album.domain.album.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tunemate.be.domain.album.domain.album.AlbumGenre;


public interface AlbumGenreRepository extends JpaRepository<AlbumGenre, Long> {

}
