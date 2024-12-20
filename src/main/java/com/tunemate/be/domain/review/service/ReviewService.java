package com.tunemate.be.domain.review.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.domain.repository.ReviewRepository;
import com.tunemate.be.domain.review.dto.request.CreateReviewDTO;
import com.tunemate.be.domain.review.dto.request.UpdateReviewDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseDTO;
import com.tunemate.be.domain.review.dto.response.ReviewResponseRepositoryDTO;
import com.tunemate.be.domain.tag.domain.Tag;
import com.tunemate.be.domain.tag.service.TagService;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.service.UserService;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final AlbumService albumService;
    private final TagService tagService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, AlbumService albumService, TagService tagService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.albumService = albumService;
        this.tagService = tagService;
    }

    public List<ReviewResponseDTO> findAlbumReview(Long albumID, Integer limit, Integer offset) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<ReviewResponseRepositoryDTO> dtos = reviewRepository.findAlbumReviewList(albumID, pageable);
        List<List<String>> tagsList = dtos.stream().map((review) -> reviewRepository.findTagsByReviewId(review.getReviewId())).toList();
        System.out.println(tagsList);
        List<ReviewResponseDTO> result = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            ReviewResponseRepositoryDTO repoDto = dtos.get(i);
            List<String> tags = tagsList.get(i);
            ReviewResponseDTO responseDTO = ReviewResponseDTO.builder().
                    albumTitle(repoDto.getAlbumTitle()).
                    albumCoverImg(repoDto.getAlbumCoverImg()).
                    reviewId(repoDto.getReviewId()).
                    nickname(repoDto.getNickname()).
                    content(repoDto.getContent()).
                    tags(tags).build();
            result.add(responseDTO);
        }
        return result;
    }

    public List<ReviewResponseRepositoryDTO> findReviewByRecent(Integer limit, Integer offset) {
        Pageable pageable = PageRequest.of(offset, limit);
        return reviewRepository.findReviewList(pageable);
    }

    public void updateReview(Long userID, Long reviewID, UpdateReviewDTO dto) {
        Review review = reviewRepository.findById(reviewID).orElseThrow(() -> new CustomException("앨범이 존재하지 않습니다.", HttpStatus.NOT_FOUND, 9001, ""));
        if (!review.getUser().getId().equals(userID)) {
            throw new CustomException("타인의 리뷰를 업데이트 하려 시도하고 있습니다.", HttpStatus.UNAUTHORIZED, 9002, "");
        }
        if (dto.getContent() != null) {
            review.setContent(dto.getContent());
        }
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            List<Tag> tags = dto.getTags().stream().map(tagService::findById).collect(Collectors.toList());
            review.setTag(tags);
        }
        try {
            reviewRepository.save(review);
        } catch (Exception e) {
            System.out.println(e);
            throw new CustomException("리뷰 업데이트에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 9003, e.getMessage());
        }
    }

    public void deleteReview(Long userID, Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new CustomException("앨범이 존재하지 않습니다.", HttpStatus.NOT_FOUND, 9001, ""));
        if (!review.getUser().getId().equals(userID)) {
            throw new CustomException("타인의 리뷰를 삭제하려 시도하고 있습니다.", HttpStatus.UNAUTHORIZED, 9002, "");
        }
        reviewRepository.deleteById(id);
    }

    public void createReview(CreateReviewDTO dto, Long userID, Long albumID) {
        try {
            User user = userService.getUserById(userID);
            Album album = albumService.getAlbumById(albumID);
            List<Tag> tags = dto.getSelectedTags().stream().map((id) -> tagService.findById(Long.valueOf(id))).toList();
            Review review = Review.builder().user(user).album(album).content(dto.getContent()).tag(tags).build();
            reviewRepository.save(review);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 8001, e.getMessage());
        }
    }
}
