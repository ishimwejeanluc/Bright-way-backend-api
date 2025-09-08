package com.brightway.brightway_dropout.dto.responsedtos;

import com.brightway.brightway_dropout.enumeration.ESchoolType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SchoolResponseDTO {
    private UUID id;
    private String name;
    private String region;
    private String address;
    private ESchoolType type;
}
