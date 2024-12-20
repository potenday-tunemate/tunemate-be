package com.tunemate.be.domain.auth.domain.emailAuth.repository;

import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    @Query("SELECT DISTINCT email_auth FROM EmailAuth email_auth WHERE email_auth.token = :token")
    Optional<EmailAuth> findByToken(@Param("token") String token);

    @Query("SELECT DISTINCT email_auth FROM EmailAuth email_auth WHERE email_auth.email= :email")
    Optional<EmailAuth> findByEmail(String email);

    @Query("SELECT DISTINCT email_auth FROM EmailAuth email_auth WHERE email_auth.id = :id")
    Optional<EmailAuth> findById(Long id);
}
