package com.tunemate.be.domain.user.domain.user;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    void create(User user);

    CreateUserDTO loginUserRight(CreateUserDTO userDto);
}
