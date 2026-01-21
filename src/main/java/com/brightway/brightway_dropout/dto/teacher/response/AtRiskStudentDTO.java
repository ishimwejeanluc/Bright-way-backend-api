package com.brightway.brightway_dropout.dto.teacher.response;

import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtRiskStudentDTO {
    private UUID studentId;
    private String studentName;
    private float dropoutProbability;
    private ERiskLevel riskLevel;
}
