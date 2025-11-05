package com.brightway.brightway_dropout.dto.grade.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeDetailDTO {
    private UUID studentId;
    private String studentName;
    private String markName;
    private Double marks;
    private String gradeType;
}
