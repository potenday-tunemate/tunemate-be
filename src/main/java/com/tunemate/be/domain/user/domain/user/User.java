package com.tunemate.be.domain.user.domain.user;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Long id;
    String email;
    String password;
    String nickname;
    Timestamp created_at;
    Timestamp updated_at;
}
