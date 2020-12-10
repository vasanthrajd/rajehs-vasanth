package com.careerin.api.dto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RegisterUserDto {

    @NotEmpty(message = "email may not be empty")
    @NotBlank(message = "email should not be empty")
    @NotNull
    private String email;

    @NotEmpty(message = "password may not be empty")
    @NotBlank(message = "password should not be empty")
    @NotNull
    private String password;

    @NotEmpty(message = "role may not be empty")
    @NotBlank(message = "role should not be empty")
    @NotNull
    private String role;
}
