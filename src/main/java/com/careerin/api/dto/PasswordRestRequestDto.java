package com.careerin.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PasswordRestRequestDto implements Serializable {

    private static final long serialVersionUID = 4602246341398388234L;

    private String password;

    private String confirmPassword;

    private String token;
}
