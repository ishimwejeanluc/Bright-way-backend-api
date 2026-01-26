package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformerDTO {
    private UUID studentId;
    private String studentName;
    private Double averageGrade;
    private Double percentileRank; // 0-100 (e.g., 95 means top 5%)
}
