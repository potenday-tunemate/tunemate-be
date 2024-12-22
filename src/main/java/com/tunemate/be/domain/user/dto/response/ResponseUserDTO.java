package com.tunemate.be.domain.user.dto.response;

import lombok.*;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDTO {
    private String nickname;
}
