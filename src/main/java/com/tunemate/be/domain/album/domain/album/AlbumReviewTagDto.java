package com.tunemate.be.domain.album.domain.album;

import lombok.Data;

@Data
public class AlbumReviewTagDto {
    private Long id;
    private int tag_id;
    private int review_id;
}
