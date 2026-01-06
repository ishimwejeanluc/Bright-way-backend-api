package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskTrendDTO {
    private String month;
    private long lowRisk;
    private long mediumRisk;
    private long highRisk;
    private long criticalRisk;
}
