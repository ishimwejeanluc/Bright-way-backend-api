package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradesReportDTO {
    private UUID studentId;
    private String studentName;
    private Double assignmentTotal;
    private Integer assignmentCount;
    private Double assignmentAverage;
    private Double finalExam;
    private Double quizTotal;
    private Integer quizCount;
    private Double quizAverage;
    private Double groupworkTotal;
    private Integer groupworkCount;
    private Double groupworkAverage;
    private Double overallAverage;
}
