package com.brightway.brightway_dropout.dto.requestdtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;



@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
