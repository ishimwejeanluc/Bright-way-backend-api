package com.brightway.brightway_dropout.dto.grade.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RegisterGradeBulkResponseDTO {
    private List<UUID> savedGradeIds;
    private int totalSaved;
    private String message;
}
