package com.careerin.api.service;

import com.careerin.api.exception.BadResourceException;
import com.careerin.api.model.PasswordReset;
import com.careerin.api.repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Value("${app.token.password.reset.duration}")
    private Long expiration;

    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetServiceImpl(final PasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    @Override
    public PasswordReset save(final PasswordReset passwordReset) {
        return passwordResetRepository.save(passwordReset);
    }

    @Override
    public Optional<PasswordReset> findByToken(final String token) {
        return passwordResetRepository.findByToken(token);
    }

    @Override
    public PasswordReset createToken() {
        final PasswordReset passwordResetToken = new PasswordReset();
        final String token = UUID.randomUUID().toString();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryAt(LocalDateTime.now().plusSeconds(expiration));
        return passwordResetToken;
    }

    @Override
    public void verifyExpiration(final PasswordReset token) {
        if (token.getExpiryAt().compareTo(LocalDateTime.now()) < 0) {
            throw new BadResourceException("Password Reset Token", "Expired token. Please issue a new request");
        }
    }
}
