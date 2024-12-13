package com.tunemate.be.domain.auth.domain.emailAuth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmailAuth {
    private Long id;
    private String email;
    private String token;
    private Timestamp expired_at;
    private Timestamp created_at;
    private Timestamp updated_at;
}
