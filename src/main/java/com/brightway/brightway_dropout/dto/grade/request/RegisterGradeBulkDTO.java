package com.brightway.brightway_dropout.dto.grade.request;

import com.brightway.brightway_dropout.enumeration.EGradeType;
import lombok.Data;
import java.util.List;

@Data
public class RegisterGradeBulkDTO {
    private String gradeName;
    private EGradeType gradeType;
    private List<StudentGradeDTO> grades;
}
