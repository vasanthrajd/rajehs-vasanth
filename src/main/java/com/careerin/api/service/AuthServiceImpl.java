package com.careerin.api.service;

import com.careerin.api.config.JwtTokenProvider;
import com.careerin.api.dto.LoginRequestDto;
import com.careerin.api.dto.PasswordRestRequestDto;
import com.careerin.api.dto.RefreshTokenDto;
import com.careerin.api.dto.RegisterUserDto;
import com.careerin.api.dto.UsersDto;
import com.careerin.api.exception.BadResourceException;
import com.careerin.api.exception.ResourceAlreadyInUseException;
import com.careerin.api.exception.ResourceNotFoundException;
import com.careerin.api.model.AuthorizeEmail;
import com.careerin.api.model.CustomUserDetails;
import com.careerin.api.model.PasswordReset;
import com.careerin.api.model.RefreshToken;
import com.careerin.api.model.Role;
import com.careerin.api.model.TokenStatus;
import com.careerin.api.model.User;
import com.careerin.api.model.UserTokenStatus;
import com.careerin.api.repository.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final AuthorizeEmailService authorizeEmailService;

    private final UserTokenStatusService userTokenStatusService;

    private final RefreshTokenService refreshTokenService;

    private final JwtTokenProvider tokenProvider;

    private final PasswordResetService passwordResetService;

    @Autowired
    private RoleRepository roleRepository;


    public AuthServiceImpl(final AuthenticationManager authenticationManager,
                           final UserService userService,
                           final AuthorizeEmailService authorizeEmailService,
                           final RefreshTokenService refreshTokenService,
                           final UserTokenStatusService userTokenStatusService,
                           final JwtTokenProvider tokenProvider,
                           final PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.authorizeEmailService = authorizeEmailService;
        this.userTokenStatusService = userTokenStatusService;
        this.refreshTokenService = refreshTokenService;
        this.tokenProvider = tokenProvider;
        this.passwordResetService = passwordResetService;
    }

    @Override
    public Boolean emailAlreadyExists(final String email) {
        return userService.existsByEmail(email);
    }

    @Override
    public Optional<UsersDto> registerUser(final UsersDto usersDto) {
        final String email = usersDto.getEmail();
        if (emailAlreadyExists(email)) {
            log.error("Email already exists: " + email);
            throw new ResourceAlreadyInUseException("Email", email, email );
        }
        log.info("Register the new User [" + email + "]");
        usersDto.setIsEmailVerified(Boolean.FALSE);
        return Optional.of(userService.save(usersDto));
    }

    @Override
    public Optional<UsersDto> registerUser(final RegisterUserDto registerUserDto) {
        final String email = registerUserDto.getEmail();
        if (emailAlreadyExists(email)) {
            log.error("Email already exists: " + email);
            throw new ResourceAlreadyInUseException("Email", email, email );
        }
        log.info("Register the new User [" + email + "]");
        UsersDto usersDto = new UsersDto();
        usersDto.setEmail(registerUserDto.getEmail());
        usersDto.setPassword(registerUserDto.getPassword());
        usersDto.setIsEmailVerified(Boolean.FALSE);
        final UsersDto savedUserDto = userService.save(registerUserDto.getRole(), usersDto);
        return Optional.of(savedUserDto);
    }

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public Optional<Authentication> authenticateUser(final LoginRequestDto userLoginRequestDto) {
    	
    	return Optional.ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequestDto.getEmail(),
                        userLoginRequestDto.getPassword())));
    }

    @Override
    public Optional<User> confirmEmailRegistration(final String emailToken) {
        final AuthorizeEmail authorizeEmail = authorizeEmailService.findByToken(emailToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Email verification", emailToken));

        final User users = authorizeEmail.getUsers();
        if (users.getIsEmailVerified()) {
            log.info("User [" + emailToken + "] already registered.");
            return Optional.of(users);
        }

        authorizeEmailService.verifyExpiration(authorizeEmail);
        authorizeEmail.setTokenStatus(TokenStatus.STATUS_CONFIRMED);
        authorizeEmailService.save(authorizeEmail);
        users.setIsEmailVerified(Boolean.TRUE);
        userService.saveUsers(users);
        return Optional.of(users);
    }

    @Override
    public Optional<AuthorizeEmail> recreateRegistrationToken(final String existingToken) {
        final AuthorizeEmail emailVerificationToken = authorizeEmailService.findByToken(existingToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Existing email verification", existingToken));

        if (emailVerificationToken.getUsers().getIsEmailVerified()) {
            return Optional.empty();
        }
        return Optional.ofNullable(authorizeEmailService.updateExistingTokenWithNameAndExpiry(emailVerificationToken));
    }

    @Override
    public Boolean currentPasswordMatches(final User currentUser, final String password) {
        return passwordEncoder.matches(password, currentUser.getPassword());
    }

    /*@Override
    public ISecurityPrincipal buildISecurityPrincipal(final Authentication authentication) {
        final CustomUser customUser = (CustomUser) authentication.getPrincipal();
        final Set<String> privilege = customUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return SecurityPrincipal.builder()
                .id(customUser.getId())
                .name(customUser.getFirstName())
                .role(customUser.getRoleFromChild())
                .privileges(privilege)
                .build();
    }*/

    @Override
    public String generateToken(final CustomUserDetails customUserDetails) {
        return tokenProvider.generateToken(customUserDetails);
    }

    @Override
    public String generateTokenFromUserId(final Long userId) {
        return tokenProvider.generateTokenFromUserId(userId);
    }

    @Override
    public Optional<RefreshToken> createAndPersistRefreshToken(final Authentication authentication,
                                                               final LoginRequestDto loginRequest) {
        User currentUser = (User) authentication.getPrincipal();
        userTokenStatusService.findByUserId(currentUser.getId())
                .map(UserTokenStatus::getRefreshToken)
                .map(RefreshToken::getId)
                .ifPresent(refreshTokenService::deleteById);

        final UserTokenStatus userTokenStatus = userTokenStatusService.createUserTokenStatus(currentUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        userTokenStatus.setRefreshToken(refreshToken);
        refreshToken.setUserTokenStatus(userTokenStatus);
        refreshToken = refreshTokenService.save(refreshToken);
        return Optional.ofNullable(refreshToken);
    }

    @Override
    public Optional<String> refreshJwtToken(final RefreshTokenDto refreshTokenDto) {
        final String token = refreshTokenDto.getRefreshToken();
        return Optional.of(refreshTokenService.findByToken(token)
                .map(refreshToken -> {
                    refreshTokenService.verifyExpiration(refreshToken);
                    userTokenStatusService.verifyRefreshAvailability(refreshToken);
                    refreshTokenService.increaseCount(refreshToken);
                    return refreshToken;
                })
                .map(RefreshToken::getUserTokenStatus)
                .map(UserTokenStatus::getUsers)
                .map(User::getId)
                .map(this::generateTokenFromUserId))
                .orElseThrow(() -> new BadResourceException("Missing Refresh Token, please login again", ""));

    }

    @Override
    public Optional<PasswordReset> generatePasswordResetToken(final Map<String, String> map) {
        final String email = map.get("email");
        return userService.findByEmail(email)
                .map(users -> {
                    final PasswordReset passwordResetToken = passwordResetService.createToken();
                    passwordResetToken.setUsers(users);
                    passwordResetService.save(passwordResetToken);
                    return Optional.of(passwordResetToken);
                })
                .orElseThrow(() -> new BadResourceException(email, "No matching user found for the given request"));

    }

    @Override
    public Optional<User> resetPassword(final PasswordRestRequestDto passwordResetRequestDto) {
        final String token = passwordResetRequestDto.getToken();
        final PasswordReset passwordReset = passwordResetService.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Password Reset Token","Password Reset request is invalid", "Token Id"));
        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        passwordResetService.verifyExpiration(passwordReset);
        final String encodedPassword = passwordEncoder.encode(passwordResetRequestDto.getPassword());

        return Optional.of(passwordReset)
                .map(PasswordReset::getUsers)
                .map(user -> {
                    user.setPassword(encodedPassword);
                    UsersDto usersDto = new UsersDto();
                    BeanUtils.copyProperties(user, usersDto);
                    usersDto = userService.save(usersDto);
                    BeanUtils.copyProperties(usersDto, user);
                    return user;
                });
    }
}
