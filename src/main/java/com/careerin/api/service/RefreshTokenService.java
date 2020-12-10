package com.careerin.api.service;


import com.careerin.api.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken createRefreshToken();

    void verifyExpiration(RefreshToken refreshToken);

    void deleteById(Long id);

    void increaseCount(RefreshToken refreshToken);

}
