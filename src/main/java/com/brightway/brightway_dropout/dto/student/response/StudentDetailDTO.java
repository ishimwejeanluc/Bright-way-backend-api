package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDetailDTO {
    private String name;
    private String riskLevel;
    private int attendance;
    private double gpa;
}
