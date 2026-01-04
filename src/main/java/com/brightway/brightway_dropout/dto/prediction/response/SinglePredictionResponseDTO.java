package com.brightway.brightway_dropout.dto.prediction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SinglePredictionResponseDTO {
    
    private UUID studentId;
    private String studentName;
    private Double probability;
    private String riskLevel;
    private String topFactor;
    private LocalDateTime predictedAt;
}
