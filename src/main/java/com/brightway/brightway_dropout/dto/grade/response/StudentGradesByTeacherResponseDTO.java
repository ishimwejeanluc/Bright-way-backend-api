package com.brightway.brightway_dropout.dto.grade.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradesByTeacherResponseDTO {
    private String courseName;
    private List<StudentGradeDetailDTO> students;
}
