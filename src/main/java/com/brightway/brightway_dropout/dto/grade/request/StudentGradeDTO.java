package com.brightway.brightway_dropout.dto.grade.request;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentGradeDTO {
    private UUID enrollmentId;
    private float marks;
}
