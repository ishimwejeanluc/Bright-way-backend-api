package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtRiskStudentDTO {
    private UUID studentId;
    private String studentName;
    private String riskLevel; // HIGH, CRITICAL
    private Double dropoutProbability;
    private Double averageGrade;
    private Double attendanceRate;
    private int behaviorIncidents;
    private String primaryConcern; // "Low Attendance", "Poor Grades", "Behavior Issues", "Multiple Factors"
}
