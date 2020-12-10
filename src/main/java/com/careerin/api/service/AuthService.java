package com.careerin.api.service;

import com.careerin.api.dto.LoginRequestDto;
import com.careerin.api.dto.PasswordRestRequestDto;
import com.careerin.api.dto.RefreshTokenDto;
import com.careerin.api.dto.RegisterUserDto;
import com.careerin.api.dto.UsersDto;
import com.careerin.api.model.AuthorizeEmail;
import com.careerin.api.model.CustomUserDetails;
import com.careerin.api.model.PasswordReset;
import com.careerin.api.model.RefreshToken;
import com.careerin.api.model.User;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

public interface AuthService {

    Boolean emailAlreadyExists(String email);

    Optional<UsersDto> registerUser(UsersDto usersDto);

    Optional<UsersDto> registerUser(RegisterUserDto registerUserDto);

    Optional<Authentication> authenticateUser(LoginRequestDto userLoginRequestDto);

    Optional<User> confirmEmailRegistration(String emailToken);

    Optional<AuthorizeEmail> recreateRegistrationToken(String existingToken);

    Boolean currentPasswordMatches(User currentUser, String password);

    String generateToken(CustomUserDetails customUserDetails);

    String generateTokenFromUserId(Long userId);

    Optional<RefreshToken> createAndPersistRefreshToken(Authentication authentication,
                                                        LoginRequestDto loginRequest);
    Optional<String> refreshJwtToken(RefreshTokenDto refreshTokenDto);

    Optional<PasswordReset> generatePasswordResetToken(Map<String, String> map);

    Optional<User> resetPassword(PasswordRestRequestDto passwordResetRequest);

}
