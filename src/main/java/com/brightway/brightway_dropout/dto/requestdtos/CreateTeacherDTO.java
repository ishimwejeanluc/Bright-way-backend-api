package com.brightway.brightway_dropout.dto.requestdtos;

import com.brightway.brightway_dropout.model.Course;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateTeacherDTO {
    // User information
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phone;

    // Teacher specific information
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "School ID is required")
    private UUID schoolId;
    @Valid
    @NotNull(message = "Teacher course is required")
    private List<Course> courses;
}
