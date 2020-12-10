
package com.careerin.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class UpdatePasswordException extends RuntimeException {

    private static final long serialVersionUID = -2022437646773783081L;
    private final String user;
    private final String message;

    public UpdatePasswordException(String user, String message) {
        super(String.format("Couldn't update password for [%s]: [%s])", user, message));
        this.user = user;
        this.message = message;
    }
}