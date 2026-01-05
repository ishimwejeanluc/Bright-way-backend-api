package com.brightway.brightway_dropout.dto.principal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrincipalDashboardOverviewResponseDTO {
    private int totalStudents;
    private int totalTeachers;
    private int totalAtRiskStudents;
    private int todayAttendance;
    private List<RiskLevelTrendDTO> riskLevelTrends;

   
}
