package com.careerin.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class UserRegistrationException extends RuntimeException {

    private static final long serialVersionUID = 117590617386500124L;
    private final String user;
    private final String message;

    public UserRegistrationException(String user, String message) {
        super(String.format("Failed to register User[%s] : '%s'", user, message));
        this.user = user;
        this.message = message;
    }

}