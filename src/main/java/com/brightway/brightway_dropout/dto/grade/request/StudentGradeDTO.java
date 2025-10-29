package com.brightway.brightway_dropout.dto.grade.request;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentGradeDTO {
    private UUID studentId;
    private float marks;
}
