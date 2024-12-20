package com.tunemate.be.domain.review.dto;

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
