package com.brightway.brightway_dropout.dto.school.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSchoolResponseDTO {
    private UUID id;
    private String name;
}
