package com.careerin.api.service;

import com.careerin.api.exception.BadResourceException;
import com.careerin.api.model.RefreshToken;
import com.careerin.api.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.token.refresh.duration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(final RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    public Optional<RefreshToken> findByToken(final String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken save(final RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken createRefreshToken() {
        final RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRefreshCount(0L);
        refreshToken.setExpiryAt(LocalDateTime.now().plusSeconds(refreshTokenDurationMs));
        return refreshToken;
    }

    @Override
    public void verifyExpiration(final RefreshToken refreshToken) {
        if (refreshToken.getExpiryAt().compareTo(LocalDateTime.now()) < 0) {
            throw new BadResourceException("Expired Token, Please issue a new request", "");
        }
    }

    @Override
    public void deleteById(final Long id) {
        refreshTokenRepository.deleteById(id);
    }

    @Override
    public void increaseCount(final RefreshToken refreshToken) {
        refreshToken.incrementRefreshCount();
        save(refreshToken);
    }

}
