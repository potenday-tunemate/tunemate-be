package com.tunemate.be.domain.album.domain.album;

import java.util.List;
import com.tunemate.be.domain.review.domain.CreateReviewDTO;
import lombok.Data;

@Data
public class AlbumReviewRequest {
    private CreateReviewDTO createReviewDTO; 
    private List<Integer> selectedTags; 
}
