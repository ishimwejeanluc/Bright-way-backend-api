package com.brightway.brightway_dropout.dto.principal.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentOverviewDTO {

    
    
        private UUID id;
        private String name;
        private String riskLevel;
        private int attendance; // percent
        private double gpa;     // average of Grade.marks
        private List<String> courses;
    }

