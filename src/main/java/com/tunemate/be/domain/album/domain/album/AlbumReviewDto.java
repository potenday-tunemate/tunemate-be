package com.tunemate.be.domain.album.domain.album;

import java.util.Date;

import lombok.Data;

@Data
public class AlbumReviewDto {
    private int id;
    private int user_id;
    private int album_id;
    private String album_review;
    private Date created_at;
}
