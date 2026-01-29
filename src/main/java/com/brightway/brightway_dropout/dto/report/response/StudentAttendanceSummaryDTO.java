package com.brightway.brightway_dropout.dto.report.response;

import java.util.List;
import java.util.UUID;

public class StudentAttendanceSummaryDTO {
    private UUID studentId;
    private String studentName;
    private Double averageAttendance;

    public StudentAttendanceSummaryDTO(UUID studentId, String studentName, Double averageAttendance) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.averageAttendance = averageAttendance;
    }

    public UUID getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public Double getAverageAttendance() { return averageAttendance; }
}
