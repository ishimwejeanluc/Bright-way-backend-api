package com.brightway.brightway_dropout.dto.teacher.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreateTeacherDTO {
    @NotBlank(message = "Teacher name is required")
    private String name;
    @NotBlank(message = "Teacher email is required")
    private String email;
    @NotBlank(message = "Teacher password is required")
    private String password;
    @NotBlank(message = "Teacher phone is required")
    private String phone;
    @NotNull(message = "School ID is required")
    private UUID schoolId;
    @NotBlank(message = "Specialization is required")
    private String specialization;
    private List<UUID> courses;
}
