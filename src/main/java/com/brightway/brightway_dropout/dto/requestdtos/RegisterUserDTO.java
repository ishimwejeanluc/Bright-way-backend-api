package com.brightway.brightway_dropout.dto.requestdtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.brightway.brightway_dropout.enumeration.EUserRole;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phone;

    private EUserRole role;
}
