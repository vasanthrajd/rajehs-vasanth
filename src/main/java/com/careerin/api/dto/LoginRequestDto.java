package com.careerin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class LoginRequestDto implements Serializable {

	private static final long serialVersionUID = -3098252329838246481L;

	@Schema(description = "User registered email", required = true, allowableValues = "NonEmpty String")
	@NotNull(message = "Login password cannot be blank")
	private String email;

	@Schema(description = "Valid user password", required = true, allowableValues = "NonEmpty String")
	@NotNull(message = "Login password cannot be blank")
	private String password;
}
