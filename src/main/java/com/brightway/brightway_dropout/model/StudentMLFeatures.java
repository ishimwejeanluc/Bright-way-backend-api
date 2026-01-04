package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "student_ml_features")
public class StudentMLFeatures extends AbstractBaseUtility {
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Attendance features
    private Double attendanceRate;
    private Integer daysAbsent;
    private Integer consecutiveAbsences;

    // Grade features
    private Double averageMarks;
    private Integer failingCoursesCount;
    private Double lowestGrade;

    // Behavior features
    private Integer incidentCount;
    private Integer severityScore;
    private Integer daysSinceLastIncident;

    // Student features
    private Integer weeksEnrolled;
    private Integer age;
    private Integer genderEncoded;  

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;
    
    @Column(name = "feature_date", nullable = false)
    private LocalDateTime featureDate;
}
