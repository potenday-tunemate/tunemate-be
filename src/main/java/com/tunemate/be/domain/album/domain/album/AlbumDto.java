package com.tunemate.be.domain.album.domain.album;

import java.util.Date;

import lombok.Data;

@Data
public class AlbumDto {
    private int id;
    private int artist_id;
    private String album_title;
    private String album_description;
    private String thumbnail_img;
    private Date release_at;
    private Date created_at;
}
