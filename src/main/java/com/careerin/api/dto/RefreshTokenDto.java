package com.careerin.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RefreshTokenDto implements Serializable {

    private static final long serialVersionUID = 9052178978317125554L;

    private String refreshToken;
}
