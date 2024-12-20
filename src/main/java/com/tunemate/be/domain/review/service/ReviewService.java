package com.tunemate.be.domain.review.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.domain.repository.ReviewRepository;
import com.tunemate.be.domain.review.dto.request.CreateReviewDTO;
import com.tunemate.be.domain.review.dto.request.PaginationDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.service.UserService;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final AlbumService albumService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, AlbumService albumService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.albumService = albumService;
    }

    public List<ReviewResponseDTO> findAlbumReview(Long albumID, Integer limit, Integer offset) {
        PaginationDTO dto = PaginationDTO.builder().limit(limit).offset(offset).build();
        return reviewRepository.findAlbumReviewList(albumID, dto);
    }

    public void createReview(CreateReviewDTO dto, Long userID, Long albumID) {
        try {
            User user = userService.getUserById(userID);
            Album album = albumService.getAlbumById(albumID);
            Review review = Review.builder().user(user).album(album).content(dto.getContent()).build();
            reviewRepository.save(review);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 8001, e.getMessage());
        }
    }
}
