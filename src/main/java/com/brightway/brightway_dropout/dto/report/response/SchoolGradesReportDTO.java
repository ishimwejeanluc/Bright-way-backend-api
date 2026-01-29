package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolGradesReportDTO {
    private UUID schoolId;
    private String schoolName;
    private int totalStudents;
    private int totalCourses;
    private int totalTeachers;
    private int totalBehaviorIncidents;
    private Double averageGrade;
    private Double highestGrade;
    private Double lowestGrade;
    private List<PerformerDTO> topPerformers;
    private List<PerformerDTO> bottomPerformers;
    private List<CourseSummaryDTO> courseSummaries;
    // List of students with their average grade for this school
    private List<StudentGradeSummaryDTO> studentSummaries;
}
