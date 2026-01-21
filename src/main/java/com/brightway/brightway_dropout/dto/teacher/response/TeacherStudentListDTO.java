package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherStudentListDTO {
    private UUID studentId;
    private String name;
    private String riskLevel;
    private Double averageAttendance;
    private Float dropoutProbability;
}
