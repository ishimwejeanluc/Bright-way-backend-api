package com.brightway.brightway_dropout.dto.requestdtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCourseDTO {
    @NotBlank(message = "Course name is required")
    private String name;
    
    private String description;
    
}
