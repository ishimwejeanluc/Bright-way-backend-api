package com.brightway.brightway_dropout.dto.course.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCourseDTO {
    @NotBlank(message = "Course name is required")
    private String name;
    private String description;
    private int credits;
    private String grade;
    private UUID schoolId;
}
