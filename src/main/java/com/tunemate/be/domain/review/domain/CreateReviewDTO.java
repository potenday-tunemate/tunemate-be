package com.tunemate.be.domain.review.domain;

import lombok.*;

@Data
@Builder
public class CreateReviewDTO {
    private Long userID;
    private Long albumID;
    private String content;
}
