package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatsResponseDTO {
    private int totalStudents;
    private int totalAtRisk;
    private double totalAttendancePercentage;
    private int totalCourses;
    private List<StudentDetailDTO> students;
}
