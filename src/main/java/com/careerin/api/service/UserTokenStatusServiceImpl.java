package com.careerin.api.service;

import com.careerin.api.exception.BadResourceException;
import com.careerin.api.model.RefreshToken;
import com.careerin.api.model.User;
import com.careerin.api.model.UserTokenStatus;
import com.careerin.api.repository.UserTokenStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserTokenStatusServiceImpl implements UserTokenStatusService {

    private final UserTokenStatusRepository userTokenStatusRepository;


    public UserTokenStatusServiceImpl(final UserTokenStatusRepository userTokenStatusRepository) {
        this.userTokenStatusRepository = userTokenStatusRepository;
    }

    @Override
    public Optional<UserTokenStatus> findByUserId(final Long userId) {
        return userTokenStatusRepository.findByUsersId(userId);
    }

    @Override
    public UserTokenStatus createUserTokenStatus(final User users) {
        final UserTokenStatus userTokenStatus = new UserTokenStatus();
        userTokenStatus.setUsers(users);
        userTokenStatus.setIsRefreshTokenActive(Boolean.TRUE);
        return userTokenStatus;
    }

    @Override
    public void verifyRefreshAvailability(final RefreshToken refreshToken) {
        final UserTokenStatus userTokenStatus = findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadResourceException("Refresh Token is not present", ""));
        if (!userTokenStatus.getIsRefreshTokenActive()) {
            throw new BadResourceException("Refresh Token is Expired, please login again", " ");
        }
    }

    @Override
    public Optional<UserTokenStatus> findByRefreshToken(final RefreshToken refreshToken) {
        return userTokenStatusRepository.findByRefreshToken(refreshToken);
    }
}
