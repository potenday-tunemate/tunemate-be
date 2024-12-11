package com.tunemate.be.domain.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class EmailAuth {
    private Long id;
    private String email;
    private String token;
    private Timestamp expired_at;
    private Timestamp created_at;
    private Timestamp updated_at;
}
