package com.tunemate.be.domain.auth.domain.emailAuth;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmailAuthDTO {
    private String email;
}
