package com.tunemate.be.domain.review.domain;

import org.apache.ibatis.annotations.Mapper;

import com.tunemate.be.domain.album.domain.album.AlbumReviewTagDTO;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<Review> findAlbumReviewList(Long albumId, PaginationDTO dto);

    List<Review> findReviewList(PaginationDTO dto);

    void create(Review review);

    void update(Review review);

    void delete(Long id);

    void registReviewTag(AlbumReviewTagDTO albumReviewTagDto);

}
