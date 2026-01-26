package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GovernmentOverallReportDTO {
    private int totalSchools;
    private int totalStudents;
    private double averageAttendance;
    private double averageGrade;
    private int totalAtRiskStudents;
    private List<SchoolOverallReportDTO> schoolReports;
}
