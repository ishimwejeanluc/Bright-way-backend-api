package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GovernmentAttendanceReportDTO {
    private String reportType;
    private int totalSchools;
    private int totalStudents;
    private double averageAttendance;
    private List<SchoolAttendanceReportDTO> schoolReports;
}
