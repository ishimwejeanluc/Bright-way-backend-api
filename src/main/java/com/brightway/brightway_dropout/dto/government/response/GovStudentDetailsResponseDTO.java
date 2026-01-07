package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovStudentDetailsResponseDTO {
    private double totalAvgMarks;
    private double totalAvgAttendance;
    private int totalCourses;
    private List<GovStudentDetailItemDTO> students;
}
