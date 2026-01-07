package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovStudentDetailItemDTO {
    private UUID studentId;
    private String name;
    private int coursesEnrolled;
    private List<String> courseNames;
    private double avgMarks;
    private double avgAttendance;
    private double probability;
    private String riskLevel;
    private int age;
    private int behaviorIncidents;
}
