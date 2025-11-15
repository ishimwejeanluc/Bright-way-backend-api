package com.brightway.brightway_dropout.dto.grade.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPerformanceTrendDTO {
    private String gradeType;
    private double averageMarks;
}
