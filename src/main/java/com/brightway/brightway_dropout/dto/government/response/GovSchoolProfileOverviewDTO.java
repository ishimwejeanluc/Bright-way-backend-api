package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovSchoolProfileOverviewDTO {
    private String schoolName;
    private String location;
    private String principalName;
    private String description;
    private int totalEnrollment;
    private int teachingStaff;
    private double dropoutRate;
    private double avgAttendance;
    private double avgGrade;
    private int behaviorIncidents;
    private int atRiskStudents;
}
