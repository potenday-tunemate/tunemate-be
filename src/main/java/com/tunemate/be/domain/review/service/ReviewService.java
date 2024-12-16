package com.tunemate.be.domain.review.service;

import com.tunemate.be.domain.album.domain.album.Album;
import com.tunemate.be.domain.album.domain.album.AlbumDto;
import com.tunemate.be.domain.album.domain.album.AlbumReviewTagDto;
import com.tunemate.be.domain.album.domain.album.ReviewTagDto;
import com.tunemate.be.domain.album.service.AlbumService;
import com.tunemate.be.domain.review.domain.CreateReviewDTO;
import com.tunemate.be.domain.review.domain.PaginationDTO;
import com.tunemate.be.domain.review.domain.Review;
import com.tunemate.be.domain.review.domain.ReviewMapper;
import com.tunemate.be.domain.user.domain.user.User;
import com.tunemate.be.domain.user.service.UserService;
import com.tunemate.be.global.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final AlbumService albumService;

    public ReviewService(ReviewMapper reviewMapper, UserService userService, AlbumService albumService) {
        this.reviewMapper = reviewMapper;
        this.userService = userService;
        this.albumService = albumService;
    }

    public List<Review> findAlbumReview(Long albumID, Integer limit, Integer offset) {
        PaginationDTO dto = PaginationDTO.builder().limit(limit).offset(offset).build();
        return reviewMapper.findAlbumReviewList(albumID, dto);
    }

    public void createReview(CreateReviewDTO dto) {
        try {
            User user = userService.getUserById(dto.getUserID());
            Album album = albumService.getAlbumById(dto.getAlbumID());
            Review review = Review.builder().user(user).album(album).content(dto.getContent()).build();
            reviewMapper.create(review);

        } catch (Exception e) {
            throw new CustomException("앨범 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, 8001, "");
        }

    }

    public void registAlbumReviewTag(int reviewId,@RequestBody Map<String, Object> selectedTags ) {


        List<Integer> selectedTagIds = (List<Integer>) selectedTags.get("selectedTagIds");
        if (selectedTagIds != null && !selectedTagIds.isEmpty()) {
            selectedTagIds.forEach(tagId -> {
                AlbumReviewTagDto albumReviewTagDto = new AlbumReviewTagDto();
                albumReviewTagDto.setTag_id(tagId);
                albumReviewTagDto.setReview_id(reviewId);
                reviewMapper.registReviewTag(albumReviewTagDto);
            });
        }
        
    }

    
}
