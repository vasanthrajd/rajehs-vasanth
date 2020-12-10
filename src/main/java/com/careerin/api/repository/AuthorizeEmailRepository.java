package com.careerin.api.repository;

import com.careerin.api.model.AuthorizeEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorizeEmailRepository extends JpaRepository<AuthorizeEmail, Long> {

    Optional<AuthorizeEmail> findByToken(String token);
}
