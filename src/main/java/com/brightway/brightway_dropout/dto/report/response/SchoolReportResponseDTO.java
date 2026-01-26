package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolReportResponseDTO {
    private UUID schoolId;
    private String schoolName;
    private String reportType; // OVERALL, ATTENDANCE, GRADES
    private int totalCourses;
    private int totalStudents;
    private List<CourseReportSection> courseReports; // Each course gets a section
}
