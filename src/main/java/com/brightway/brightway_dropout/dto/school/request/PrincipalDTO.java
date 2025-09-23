package com.brightway.brightway_dropout.dto.school.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrincipalDTO {
    @NotBlank(message = "Principal name is required")
    private String name;
    @Email(message = "Principal email is required")
    private String email;
    @NotBlank(message = "Principal password is required")
    private String password;
    @NotBlank(message = "Principal phone is required")
    private String phone;
}
