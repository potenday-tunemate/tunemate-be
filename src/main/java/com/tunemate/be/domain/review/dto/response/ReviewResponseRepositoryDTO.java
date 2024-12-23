package com.tunemate.be.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReviewResponseRepositoryDTO {
    private Long reviewId;
    private String nickname;
    private String artistName;
    private String albumTitle;
    private String albumCoverImg;
    private String content;
}
