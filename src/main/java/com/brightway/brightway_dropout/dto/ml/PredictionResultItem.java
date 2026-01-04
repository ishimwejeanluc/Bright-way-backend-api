package com.brightway.brightway_dropout.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResultItem {
    
    private UUID studentId;
    private Double probability;     // 0.0 - 1.0
    private String riskLevel;       // LOW, MEDIUM, HIGH, CRITICAL
    private List<TopFactor> topFactors;  // Can be null or empty
}
