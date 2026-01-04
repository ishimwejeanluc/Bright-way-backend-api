package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "dropout_predictions")
public class DropoutPrediction extends AbstractBaseUtility {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private float probability;

    @Enumerated(EnumType.STRING)
    private ERiskLevel riskLevel;
    
    private String topFactor;  // The most significant factor (e.g., "Low Attendance Rate")
    
    @Column(length = 500)
    private String factorMessage;  // Detailed message about the factor
    
    private String status;  // ACTIVE, RESOLVED, MONITORING
    
    private LocalDateTime predictedAt;
}
