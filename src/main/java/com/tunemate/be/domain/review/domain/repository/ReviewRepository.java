package com.tunemate.be.domain.review.domain.repository;

import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.dto.response.AlbumVinylDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT new com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO(review.id, user.nickname, artist.name, album.title, album.coverImg, review.content) FROM Review review JOIN review.user user JOIN review.album album JOIN review.album.artist artist WHERE review.id = :id")
    List<ReviewResponseRepositoryDTO> findAlbumReviewList(@Param("id") Long id, Pageable pageable);

    @Query("SELECT tag.name FROM Review review JOIN review.tag tag WHERE review.id = :id")
    List<String> findTagsByReviewId(@Param("id") Long id);

    @Query("SELECT new com.tunemate.be.domain.review.dto.response.AlbumVinylDTO(tag.name, COUNT(tag.id)) FROM Review review JOIN review.tag tag JOIN review.album album WHERE album.id = :id GROUP BY tag.name ORDER BY COUNT(tag.id) DESC")
    List<AlbumVinylDTO> findAlbumVinylList(@Param("id") Long id);


    @Query("SELECT new com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO(review.id, user.nickname, artist.name, album.title, album.coverImg, review.content) FROM Review review JOIN review.user user JOIN review.album album JOIN review.album.artist artist ORDER BY review.createdAt DESC")
    List<ReviewResponseRepositoryDTO> findReviewList(Pageable pageable);

    @Query("SELECT review FROM Review review WHERE review.id = :id")
    Optional<Review> findById(Long id);
}
