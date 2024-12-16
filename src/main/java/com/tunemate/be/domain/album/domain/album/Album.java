package com.tunemate.be.domain.album.domain.album;

import com.tunemate.be.domain.artist.domain.artist.Artist;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
public class Album {
    private Long id;
    private String title;
    private String coverImg;
    private Artist artist;
    private Integer year;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
