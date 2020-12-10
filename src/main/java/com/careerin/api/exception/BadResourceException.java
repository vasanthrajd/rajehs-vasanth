package com.careerin.api.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class BadResourceException extends RuntimeException {

    private static final long serialVersionUID = 6325939251694481235L;

    private final String resourceName;
    private final String fieldName;

    public BadResourceException(final String resourceName, final String fieldName) {
        super(String.format("%s already in use with %s", resourceName, fieldName));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
    }
}
