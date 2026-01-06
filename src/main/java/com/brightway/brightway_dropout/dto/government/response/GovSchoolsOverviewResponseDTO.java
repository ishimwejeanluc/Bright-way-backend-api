package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovSchoolsOverviewResponseDTO {
    private int totalSchools;
    private int totalStudents;
    private double overallDropoutRate;
    private int totalAtRiskStudents;
    private List<SchoolOverviewItemDTO> schools;
}
