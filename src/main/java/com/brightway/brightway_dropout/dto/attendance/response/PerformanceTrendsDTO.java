package com.brightway.brightway_dropout.dto.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceTrendsDTO {
    private List<SubjectPerformanceDTO> subjectPerformance;
    private OverallStatsDTO overallStats;
}