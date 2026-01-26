package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolOverallReportDTO {
    private UUID schoolId;
    private String schoolName;
    
    // School-wide statistics
    private int totalStudents;
    private int totalCourses;
    private int totalTeachers;
    private int totalBehaviorIncidents;
    
    // Grade Statistics (across all courses)
    private Double averageGrade;
    private Double highestGrade;
    private Double lowestGrade;
    
    // Attendance Statistics (across all courses)
    private Double averageAttendance;
    private Double highestAttendance;
    private Double lowestAttendance;
    
    // Risk Level Distribution
    private RiskDistributionDTO riskDistribution;
    private Double averageDropoutProbability;
    
    // Top and Bottom Performers (across all courses)
    private List<PerformerDTO> topPerformers;
    private List<PerformerDTO> bottomPerformers;
    
    // At-Risk Students (across all courses)
    private List<AtRiskStudentDTO> atRiskStudents;
    
    // Course-level summary
    private List<CourseSummaryDTO> courseSummaries;
}
