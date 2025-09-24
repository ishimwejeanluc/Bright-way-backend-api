package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudentWithParentResponseDTO {
    private UUID studentId;
    private UUID parentId;
    private String message;
}
