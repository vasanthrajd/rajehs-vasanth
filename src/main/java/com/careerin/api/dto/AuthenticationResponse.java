package com.careerin.api.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 630839444781197036L;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiryDuration;

    public AuthenticationResponse(final String accessToken, final String refreshToken, final Long expiryDuration) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiryDuration = expiryDuration;
        tokenType = "Bearer ";
    }

}
