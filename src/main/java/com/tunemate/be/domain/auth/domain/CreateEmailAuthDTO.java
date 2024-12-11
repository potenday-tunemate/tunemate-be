package com.tunemate.be.domain.auth.domain;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record CreateEmailAuthDTO(
        String email,
        String token,
        Timestamp expired_at
) {

}
