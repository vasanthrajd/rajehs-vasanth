package com.careerin.api.controller;

import com.careerin.api.config.JwtTokenProvider;
import com.careerin.api.dto.ApiResponseDto;
import com.careerin.api.dto.AuthenticationResponse;
import com.careerin.api.dto.LoginRequestDto;
import com.careerin.api.dto.PasswordRestRequestDto;
import com.careerin.api.dto.RefreshTokenDto;
import com.careerin.api.dto.RegisterUserDto;
import com.careerin.api.event.GenerateResetLinkEvent;
import com.careerin.api.event.RegenerateEmailVerificationEvent;
import com.careerin.api.event.UserAccountChangeEvent;
import com.careerin.api.event.UserRegistrationCompleteEvent;
import com.careerin.api.exception.BadResourceException;
import com.careerin.api.exception.InvalidTokenRequestException;
import com.careerin.api.exception.PasswordResetException;
import com.careerin.api.exception.PasswordResetLinkException;
import com.careerin.api.exception.TokenRefreshException;
import com.careerin.api.exception.UserLoginException;
import com.careerin.api.exception.UserRegistrationException;
import com.careerin.api.model.AuthorizeEmail;
import com.careerin.api.model.CustomUserDetails;
import com.careerin.api.model.RefreshToken;
import com.careerin.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class SignInAndSignUpController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public SignInAndSignUpController(AuthService authService,
                                     JwtTokenProvider tokenProvider,
                                     ApplicationEventPublisher applicationEventPublisher) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Checks is a given email is in use or not.
     */
    @Operation(summary="Checks the email",description = "Checks if the given email is in use")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns whether email exists or not",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDto.class))) })
    @GetMapping("/checkEmailInUse")
    public ResponseEntity<Boolean> checkEmailInUse(@Parameter(description = "Email id to check against")
                                              @RequestParam("email") final String email) {
        Boolean emailExists = authService.emailAlreadyExists(email);
        return ResponseEntity.ok(emailExists);
    }

    @Operation(summary="Logs in the user",description = "Logs the user in to the system and return the auth tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log in",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDto.class)))})
    @PostMapping("/login")
    public ResponseEntity authenticateUser(
            @Parameter(description = "The LoginRequest payload") @Valid @RequestBody final LoginRequestDto loginRequestDto) {

        Authentication authentication = authService.authenticateUser(loginRequestDto)
                .orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequestDto + "]"));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authService.createAndPersistRefreshToken(authentication, loginRequestDto)
                .map(RefreshToken::getToken)
                .map(refreshToken -> {
                    String jwtToken = authService.generateToken(customUserDetails);
                    return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken, tokenProvider.getExpiryDuration()));
                })
                .orElseThrow(() -> new UserLoginException("Couldn't create refresh token for: [" + loginRequestDto + "]"));
    }

    /**
     * Entry point for the user registration process. On successful registration,
     * publish an event to generate email verification token
     */
    @Operation(summary="Registers the user",description = "Registers the user and publishes an event to generate the email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Register",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDto.class)))})
    @PostMapping("/register")
    public ResponseEntity registerUser(@Parameter(description = "The RegistrationRequest payload") @Valid @RequestBody final RegisterUserDto registerUserDto) {

        return authService.registerUser(registerUserDto)
                .map(user -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/auth/registrationConfirmation");
                    UserRegistrationCompleteEvent userRegistrationCompleteEvent = new UserRegistrationCompleteEvent(user, urlBuilder);
                    applicationEventPublisher.publishEvent(userRegistrationCompleteEvent);
                    log.info("Registered User returned [API[: " + user);
                    return ResponseEntity.ok(ApiResponseDto.builder()
                            .data("User registered successfully. Check your email for verification").success(true).build());
                })
                .orElseThrow(() -> new UserRegistrationException(registerUserDto.getEmail(), "Missing user object in database"));
    }

    /**
     * Receives the reset link request and publishes an event to send email id containing
     * the reset link if the request is valid. In future the deeplink should open within
     * the app itself.
     */
    @Operation(summary="Password Reset Link",description = "Receive the reset link request and publish event to send mail containing the password "
            + "reset link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password Reset Link",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDto.class)))})
    public ResponseEntity resetLink(@Parameter(description = "The PasswordResetLinkRequest payload") @Valid @RequestBody final Map<String, String> map) {
        if (map.containsKey("email")) {
            return authService.generatePasswordResetToken(map)
                    .map(passwordResetToken -> {
                        UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/password/reset");
                        GenerateResetLinkEvent generateResetLinkMailEvent = new GenerateResetLinkEvent(urlBuilder,
                                passwordResetToken);
                        applicationEventPublisher.publishEvent(generateResetLinkMailEvent);
                        return ResponseEntity.ok(ApiResponseDto.builder().data("Password reset link sent successfully")
                                .success(true).build());
                    })
                    .orElseThrow(() -> new PasswordResetLinkException(map.get("email"), "Couldn't create a valid token"));
        } else {
            throw new BadResourceException(map.get("email"), "Couldn't reset link");
        }
    }

    /**
     * Receives a new passwordResetRequest and sends the acknowledgement after
     * changing the password to the user's mail through the event.
     */


    @Operation(summary="Resets the password",description = "Reset the password after verification and publish an event to send the acknowledgement "
            + "email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password Reset after verification",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDto.class)))})
    @PostMapping("/password/reset")
    public ResponseEntity resetPassword(@Parameter(description = "The PasswordResetRequest payload") @Valid @RequestBody final PasswordRestRequestDto passwordRestRequestDto) {

        return authService.resetPassword(passwordRestRequestDto)
                .map(changedUser -> {
                    UserAccountChangeEvent onPasswordChangeEvent = new UserAccountChangeEvent(changedUser, "Reset Password",
                            "Changed Successfully");
                    applicationEventPublisher.publishEvent(onPasswordChangeEvent);
                    return ResponseEntity.ok(ApiResponseDto.builder().success(true).data("Password changed successfully").build());
                })
                .orElseThrow(() -> new PasswordResetException(passwordRestRequestDto.getToken(), "Error in resetting password"));
    }

    /**
     * Confirm the email verification token generated for the user during
     * registration. If token is invalid or token is expired, report error.
     */
    @Operation(summary="Confirms the registration",description = "Confirms the email verification token that has been generated for the user during registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration Confirmation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ApiResponseDto.class)))) })
    @GetMapping("/registrationConfirmation")

    public ResponseEntity confirmRegistration(@Parameter(description = "the token that was sent to the user email") @RequestParam("token") String token) {

        return authService.confirmEmailRegistration(token)
                .map(user -> ResponseEntity.ok(ApiResponseDto.builder().data("User verified successfully").success(true).build()))
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", token, "Failed to confirm. Please generate a new email verification request"));
    }

    /**
     * Resend the email registration mail with an updated token expiry. Safe to
     * assume that the user would always click on the last re-verification email and
     * any attempts at generating new token from past (possibly archived/deleted)
     * tokens should fail and report an exception.
     */
    @Operation(summary="Resends the registration token",description = "Resend the email registration with an updated token expiry. Safe to "
            + "assume that the user would always click on the last re-verification email and "
            + "any attempts at generating new token from past (possibly archived/deleted)"
            + "tokens should fail and report an exception. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resend Registration Token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ApiResponseDto.class)))) })
    @GetMapping("/resendRegistrationToken")

    public ResponseEntity resendRegistrationToken(@Parameter(description = "the initial token that was sent to the user email after registration") @RequestParam("token") final String existingToken) {

        AuthorizeEmail newEmailToken = authService.recreateRegistrationToken(existingToken)
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken, "User is already registered. No need to re-generate token"));

        return Optional.ofNullable(newEmailToken.getUsers())
                .map(registeredUser -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/auth/registrationConfirmation");
                    RegenerateEmailVerificationEvent regenerateEmailVerificationEvent = new RegenerateEmailVerificationEvent(registeredUser, urlBuilder, newEmailToken);
                    applicationEventPublisher.publishEvent(regenerateEmailVerificationEvent);
                    return ResponseEntity.ok(ApiResponseDto.builder().success(true).data("Email verification resent successfully").build());
                })
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken, "No user associated with this request. Re-verification denied"));
    }

    /**
     * Refresh the expired jwt token using a refresh token for the specific device
     * and return a new token to the caller
     */

    @Operation(summary="Refresh the expired jwt authentication",description = "Refresh the expired jwt authentication by issuing a token refresh request and returns the"
            + "updated response tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseDto.class)))})
    @PostMapping("/refresh")
    public ResponseEntity refreshJwtToken(@Parameter(description = "The TokenRefreshRequest payload") @Valid @RequestBody final RefreshTokenDto refreshTokenDto) {

        return authService.refreshJwtToken(refreshTokenDto)
                .map(updatedToken -> {
                    String refreshToken = refreshTokenDto.getRefreshToken();
                    log.info("Created new Jwt Auth token: " + updatedToken);
                    return ResponseEntity.ok(new AuthenticationResponse(updatedToken, refreshToken, tokenProvider.getExpiryDuration()));
                })
                .orElseThrow(() -> new TokenRefreshException(refreshTokenDto.getRefreshToken(), "Unexpected error during token refresh. Please logout and login again."));
    }
}
