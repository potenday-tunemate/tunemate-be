package com.tunemate.be.domain.user.domain.user.repository;

import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT user FROM User user WHERE user.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT user FROM User user WHERE user.id= :id")
    Optional<User> findById(@Param("id") Long id);

    @Query("SELECT new com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO(review.id, user.nickname, artist.name, album.title, album.coverImg, review.content)"+
            " FROM Review review JOIN review.user user JOIN review.album album JOIN review.album.artist artist " +
            "WHERE review.user.id = :userId " +
            "ORDER BY review.createdAt DESC")
    List<ReviewResponseRepositoryDTO> fingUserReviewList(@Param("userId") Long userId);
}
