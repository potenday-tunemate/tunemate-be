package com.tunemate.be.domain.user.domain.user.repository;


import com.tunemate.be.domain.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT user FROM User user WHERE user.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT user FROM User user WHERE user.id= :id")
    Optional<User> findById(@Param("id") Long id);
}
