package com.brightway.brightway_dropout.dto.principal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.brightway.brightway_dropout.dto.principal.response.StudentOverviewDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrincipalStudentOverviewResponseDTO {
    private int totalStudents;
    private int totalAtRiskStudents;
    private int todayAttendance;
    private List<StudentOverviewDTO> students;
}
