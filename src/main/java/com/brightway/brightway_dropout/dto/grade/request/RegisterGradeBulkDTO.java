package com.brightway.brightway_dropout.dto.grade.request;

import com.brightway.brightway_dropout.enumeration.EGradeType;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class RegisterGradeBulkDTO {
    private UUID courseId;
    private String gradeName;
    private EGradeType gradeType;
    private List<StudentGradeDTO> grades;
}
