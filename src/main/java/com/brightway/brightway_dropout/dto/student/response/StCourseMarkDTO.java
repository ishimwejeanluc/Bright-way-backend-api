package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StCourseMarkDTO {
    private String type; // Quiz, Assignment, Exam, etc.
    private String title;
    private Double score;

    
}
