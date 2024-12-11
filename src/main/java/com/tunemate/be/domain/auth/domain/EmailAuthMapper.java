package com.tunemate.be.domain.auth.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface EmailAuthMapper {
    Optional<EmailAuth> findByToken(String token);

    void create(CreateEmailAuthDTO dto);
}
