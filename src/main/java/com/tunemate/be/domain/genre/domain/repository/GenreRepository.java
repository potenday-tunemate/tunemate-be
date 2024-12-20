package com.tunemate.be.domain.genre.domain.repository;

import com.tunemate.be.domain.genre.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT genre FROM Genre genre WHERE genre.name = :name")
    Optional<Genre> findByName(String name);
}
