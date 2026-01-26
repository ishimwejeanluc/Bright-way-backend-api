package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskDistributionDTO {
    private int lowRiskCount;
    private int mediumRiskCount;
    private int highRiskCount;
    private int criticalRiskCount;
    private Double lowRiskPercentage;
    private Double mediumRiskPercentage;
    private Double highRiskPercentage;
    private Double criticalRiskPercentage;
    private String trend; // "IMPROVING", "STABLE", "CONCERNING", "CRITICAL"
}
