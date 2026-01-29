package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolAttendanceReportDTO {
    private UUID schoolId;
    private String schoolName;
    private int totalStudents;
    private int totalCourses;
    private int totalTeachers;
    private int totalBehaviorIncidents;
    private Double averageAttendance;
    private Double highestAttendance;
    private Double lowestAttendance;
    private List<CourseSummaryDTO> courseSummaries;
    // List of students with their average attendance for this school
    private List<StudentAttendanceSummaryDTO> studentSummaries;
}
