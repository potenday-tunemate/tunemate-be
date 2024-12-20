package com.tunemate.be.domain.album.domain.album;

import lombok.Data;

import java.util.List;

@Data
public class AlbumReviewRequest {
    private Long userID;
    private Long albumID;
    private String content;
    private List<Integer> selectedTags;
}
