package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnhancedCourseOverallSummaryDTO {
    // Basic Stats
    private int totalStudents;
    private int totalBehaviorIncidents;
    
    // Grade Statistics
    private Double averageGrade;
    private Double highestGrade;
    private Double lowestGrade;
    
    // Attendance Statistics
    private Double averageAttendance;
    private Double highestAttendance;
    private Double lowestAttendance;
    
    // Risk Level Distribution
    private RiskDistributionDTO riskDistribution;
    private Double averageDropoutProbability;
    
    // Top and Bottom Performers
    private List<PerformerDTO> topPerformers; // Top 10%
    private List<PerformerDTO> bottomPerformers; // Bottom 10%
    
    // At-Risk Students
    private List<AtRiskStudentDTO> atRiskStudents;
}
