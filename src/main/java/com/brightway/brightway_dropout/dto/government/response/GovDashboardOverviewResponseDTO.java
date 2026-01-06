package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovDashboardOverviewResponseDTO {
    private SummaryDTO summary;
    private List<RiskTrendDTO> dropoutRiskTrends;
    private List<PolicyImpactDTO> policyImpactBySchool;
}
