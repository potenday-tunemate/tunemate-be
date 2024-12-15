package com.tunemate.be.domain.user.domain.user;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Long id;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email;
    String password;
    String nickname;
    Timestamp createdAt;
    Timestamp updatedAt;
}
