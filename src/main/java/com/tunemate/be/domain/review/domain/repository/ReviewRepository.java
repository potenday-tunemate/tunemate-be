package com.tunemate.be.domain.review.domain.repository;

import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.dto.PaginationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT review, user.nickname, album.title FROM Review review JOIN User user ON review.user = user JOIN Album album ON review.album = album WHERE review.id = :id")
    List<Review> findAlbumReviewList(@Param("id") Long id, @Param("dto") PaginationDTO dto);

    @Query("SELECT review FROM Review review")
    List<Review> findReviewList(PaginationDTO dto);
}
