package com.tunemate.be.domain.review.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateReviewDTO {
    private Long userID;
    private Long albumID;
    private String content;
    private List<Integer> selectedTags;
}
