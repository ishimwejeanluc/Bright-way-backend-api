package com.brightway.brightway_dropout.dto.school.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolStatisticsResponseDTO {
    private int totalStudents;
    private int totalTeachers;
    private double dropoutRate;
    private double attendanceRate;
    private String riskTrends;
    private String policyImpact;
}
