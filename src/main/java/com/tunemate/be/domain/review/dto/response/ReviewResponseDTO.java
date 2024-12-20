package com.tunemate.be.domain.review.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ReviewResponseDTO extends ReviewResponseRepositoryDTO {
    private List<String> tags;
}
