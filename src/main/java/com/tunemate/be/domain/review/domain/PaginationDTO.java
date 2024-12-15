package com.tunemate.be.domain.review.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDTO {
    private Integer limit;
    private Integer offset;
}
