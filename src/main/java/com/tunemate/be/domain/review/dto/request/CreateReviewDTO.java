package com.tunemate.be.domain.review.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateReviewDTO {
    private String content;
    private List<Integer> selectedTags;
}
