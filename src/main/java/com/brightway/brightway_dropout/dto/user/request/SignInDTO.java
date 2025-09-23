package com.brightway.brightway_dropout.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInDTO {
    @Email(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
