package com.tunemate.be.domain.auth.domain.emailAuth;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface EmailAuthMapper {
    Optional<EmailAuth> findByToken(String token);

    Optional<EmailAuth> findByEmail(String email);

    Optional<EmailAuth> findById(Long id);


    void update(UpdateEmailAuthDTO dto);

    void create(CreateEmailAuthDTO dto);
}
