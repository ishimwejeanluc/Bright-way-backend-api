package com.brightway.brightway_dropout.dto.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectPerformanceDTO {
    private String subjectName;
    private Double currentAverage;
}