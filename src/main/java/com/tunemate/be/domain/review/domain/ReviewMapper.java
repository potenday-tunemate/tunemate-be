package com.tunemate.be.domain.review.domain;

import com.tunemate.be.domain.album.domain.album.AlbumReviewTagDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("""
            SELECT r.id         AS review_id,
               r.content    AS review_content,
               r.created_at AS review_created_at,
               r.updated_at AS review_updated_at, 
               u.id         AS user_id,
               u.nickname   AS user_nickname, 
               a.id         AS album_id
            FROM review AS r
            JOIN user AS u ON r.user = u.id
            JOIN album AS a ON r.album = a.id
            WHERE r.id = #{albumId}
            LIMIT #{dto.limit}
            OFFSET #{dto.offset}
            """)
    List<Review> findAlbumReviewList(@Param("albumId") Long albumId, @Param("dto") PaginationDTO dto);

    @Select("SELECT * FROM review ORDER BY ASC LIMIT #{dto.limit} OFFSET #{dto.offset}")
    List<Review> findReviewList(PaginationDTO dto);

    @Insert("""
            INSERT INTO review (user, album, content)
            VALUES (#{review.user.id}, #{review.album.id}, #{review.content})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "review.id")
    void create(@Param("review") Review review);

    void update(Review review);

    void delete(Long id);

    @Insert("INSERT INTO review_tag (tag_id, review_id) VALUES (#{dto.tag_id},#{dto.review_id})")
    void registReviewTag(@Param("dto") AlbumReviewTagDto albumReviewTagDto);

}
