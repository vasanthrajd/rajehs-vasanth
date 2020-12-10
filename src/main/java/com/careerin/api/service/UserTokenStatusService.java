package com.careerin.api.service;


import com.careerin.api.model.RefreshToken;
import com.careerin.api.model.User;
import com.careerin.api.model.UserTokenStatus;

import java.util.Optional;

public interface UserTokenStatusService {

    Optional<UserTokenStatus> findByUserId(Long userId);

    UserTokenStatus createUserTokenStatus(User users);

    void verifyRefreshAvailability(RefreshToken refreshToken);

    Optional<UserTokenStatus> findByRefreshToken(RefreshToken refreshToken);
}
