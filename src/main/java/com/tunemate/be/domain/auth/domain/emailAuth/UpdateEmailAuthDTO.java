package com.tunemate.be.domain.auth.domain.emailAuth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@Getter
@Setter
public class UpdateEmailAuthDTO {
    private Long id;
    private String token;
    @JsonProperty("expired_at")
    private Instant expiredAt;
}
