package com.tunemate.be.domain.auth.domain.emailAuth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmailAuthDTO {
    private String email;
    private String token;

    @JsonProperty("expired_at")
    private Instant expiredAt;
}
