package com.tunemate.be.domain.review.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    private Long userID;
    private Long albumID;
    private String content;
}
