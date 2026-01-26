package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSummaryDTO {
    private UUID courseId;
    private String courseName;
    private String teacherName;
    private int studentCount;
    private Double averageGrade;
    private Double attendanceRate;
    private int atRiskCount;
}
