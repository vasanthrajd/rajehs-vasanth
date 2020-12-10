package com.careerin.api.service;


import com.careerin.api.dto.UsersDto;
import com.careerin.api.model.AuthorizeEmail;

import java.util.Optional;

public interface AuthorizeEmailService {


    void createVerificationToken(UsersDto usersDto, String token);

    String generateNewToken();

    AuthorizeEmail updateExistingTokenWithNameAndExpiry(AuthorizeEmail authorizeEmail);

    Optional<AuthorizeEmail> findByToken(String token);

    void verifyExpiration(AuthorizeEmail authorizeEmail);

    AuthorizeEmail save(AuthorizeEmail authorizeEmail);
}
