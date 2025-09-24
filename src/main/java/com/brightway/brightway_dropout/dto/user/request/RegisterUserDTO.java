package com.brightway.brightway_dropout.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    private String phone;

}
