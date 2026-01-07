package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovTeacherDetailsResponseDTO {
    private int totalCourses;
    private int totalStudents;
    private List<GovTeacherDetailItemDTO> teachers;
}
