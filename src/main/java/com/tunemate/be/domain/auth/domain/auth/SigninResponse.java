package com.tunemate.be.domain.auth.domain.auth;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SigninResponse {
    private String accessToken;
    private String refreshToken;
}
