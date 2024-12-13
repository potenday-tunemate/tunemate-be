package com.tunemate.be.domain.auth.domain.auth;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SigninDTO {
    private String email;
    private String password;
}
