
package com.careerin.api.controller;

import com.careerin.api.annotation.CurrentUser;
import com.careerin.api.dto.ApiResponseDto;
import com.careerin.api.dto.UsersDto;
import com.careerin.api.event.UserLogoutSuccessEvent;
import com.careerin.api.exception.UpdatePasswordException;
import com.careerin.api.model.CustomUserDetails;
import com.careerin.api.model.User;
import com.careerin.api.service.AuthService;
import com.careerin.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Log4j2
public class UserController {


    private final AuthService authService;

    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserController(AuthService authService, UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.authService = authService;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Operation(summary = "Logs the specified user device and clears the refresh tokens associated with it",
            description = "Logs the specified user device and clears the refresh tokens associated with it", tags = { "users" } )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsersDto.class))) })
    @GetMapping("/user-info/{id}")
    public ResponseEntity<UsersDto> getUserProfile(@PathVariable("id") final Long id) {
        UsersDto user = userService.userFindById(id);
        return ResponseEntity.ok().body(user);
    }
    /*
    @Operation(summary = "Get existing userInfo by Id", description = "Returns a single userInfo", tags = { "users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a single userInfo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsersDto.class))) })
    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable("id") final Long id) {
        UsersDto user = userService.userFindById(id);
        return ResponseEntity.ok().body(user);
    }
*/



    /**
     * Updates the password of the current logged in user
     */
    /*@PostMapping("/password/update")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Allows the user to change his password once logged in by supplying the correct current " +
            "password")
    public ResponseEntity updateUserPassword(@CurrentUser CustomUserDetails customUserDetails,
                                             @ApiParam(value = "The UpdatePasswordRequest payload") @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {

        return authService.updatePassword(customUserDetails, updatePasswordRequest)
                .map(updatedUser -> {
                    OnUserAccountChangeEvent onUserPasswordChangeEvent = new OnUserAccountChangeEvent(updatedUser, "Update Password", "Change successful");
                    applicationEventPublisher.publishEvent(onUserPasswordChangeEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
                })
                .orElseThrow(() -> new UpdatePasswordException("--Empty--", "No such user present."));
    }*/

    @Operation(summary = "Logs the specified user device and clears the refresh tokens associated with it",
            description = "Logs the specified user device and clears the refresh tokens associated with it", tags = { "users" } )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsersDto.class))) })
    @PostMapping("/logout")
    public ResponseEntity logoutUser(@CurrentUser CustomUserDetails customUserDetails) throws LoginException {
        userService.logoutUser(customUserDetails.getId());
        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();

        UserLogoutSuccessEvent logoutSuccessEvent = new UserLogoutSuccessEvent(customUserDetails.getEmail(), credentials.toString());
        applicationEventPublisher.publishEvent(logoutSuccessEvent);
        return ResponseEntity.ok(ApiResponseDto.builder()
                .data("User registered successfully. Check your email for verification").success(true).build());
    }
}
