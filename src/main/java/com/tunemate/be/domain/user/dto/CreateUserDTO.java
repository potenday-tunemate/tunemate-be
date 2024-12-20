package com.tunemate.be.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email;
    String password;
    String nickname;
}
