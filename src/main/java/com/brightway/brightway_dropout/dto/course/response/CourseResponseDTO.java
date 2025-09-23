package com.brightway.brightway_dropout.dto.course.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {
    private UUID id;
    private String name;
    private String description;
}
