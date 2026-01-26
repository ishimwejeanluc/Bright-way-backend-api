package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceReportDTO {
    private UUID studentId;
    private String studentName;
    private Map<String, String> attendanceByDate; // Date -> Status (PRESENT/ABSENT)
    private Double attendanceRate;
}
