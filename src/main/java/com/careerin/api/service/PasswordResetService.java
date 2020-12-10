package com.careerin.api.service;


import com.careerin.api.model.PasswordReset;

import java.util.Optional;

public interface PasswordResetService {

    PasswordReset save(PasswordReset passwordResetToken);

    Optional<PasswordReset> findByToken(String token);

    PasswordReset createToken();

    void verifyExpiration(PasswordReset token);
}
