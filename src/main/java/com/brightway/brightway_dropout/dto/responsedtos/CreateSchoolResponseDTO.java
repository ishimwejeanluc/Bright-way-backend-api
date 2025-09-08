package com.brightway.brightway_dropout.dto.responsedtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateSchoolResponseDTO {
    private UUID id;
    private String name;

}
