package com.brightway.brightway_dropout.dto.prediction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPredictionProfileDTO {
    
    private UUID studentId;
    private String studentName;
    
    // Dropout Prediction
    private Double probability;
    private String riskLevel;
    private String topFactor;
    private LocalDateTime predictedAt;
    
    // Academic Performance
    private Double averageMarks;
    private Double lowestGrade;
    private Integer failingCoursesCount;
    private Integer weeksEnrolled;
    
    // Attendance Metrics
    private Double attendanceRate;
    private Integer daysAbsent;
    private Integer consecutiveAbsences;
    
    // Behavior & Incidents
    private Integer incidentCount;
    private Integer severityScore;
    private Integer daysSinceLastIncident;
    
    // Personal Information
    private Integer age;
    private String gender;
}
