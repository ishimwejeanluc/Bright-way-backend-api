package com.brightway.brightway_dropout.dto.student.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDetailDTO {
    private UUID studentId;
    private String name;
    private String riskLevel;
    private Integer attendance;
    private Double gpa;

    public StudentDetailDTO(UUID studentId, Integer attendance, String name) {
        this.studentId = studentId;
        this.attendance = attendance;
        this.name = name;

    }
     public StudentDetailDTO(UUID studentId, String name) {
        this.studentId = studentId;
        this.name = name;

    }
}
