package com.tunemate.be.domain.review.domain.repository;

import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.dto.request.PaginationDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT new com.tunemate.be.domain.review.dto.response.ReviewResponseDTO(user.nickname, album.title, album.coverImg, review.content) FROM Review review JOIN review.user user JOIN review.album album WHERE review.id = :id")
    List<ReviewResponseDTO> findAlbumReviewList(@Param("id") Long id, @Param("dto") PaginationDTO dto);

    @Query("SELECT review FROM Review review")
    List<Review> findReviewList(PaginationDTO dto);
}
