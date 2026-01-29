package com.brightway.brightway_dropout.dto.report.response;

import java.util.UUID;

public class StudentGradeSummaryDTO {
    private UUID studentId;
    private String studentName;
    private Double averageGrade;

    public StudentGradeSummaryDTO(UUID studentId, String studentName, Double averageGrade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.averageGrade = averageGrade;
    }

    public UUID getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public Double getAverageGrade() { return averageGrade; }
}
