package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class CourseSummaryDTO {
    private UUID courseId;
    private String courseName;
    private String teacherName;
    private int studentCount;
    private Double averageGrade;
    private Double attendanceRate;
    private int atRiskCount;

    public CourseSummaryDTO(UUID courseId, String courseName, String teacherName, int studentCount, Double attendanceRate) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.studentCount = studentCount;
        this.attendanceRate = attendanceRate;
    }

    
}
