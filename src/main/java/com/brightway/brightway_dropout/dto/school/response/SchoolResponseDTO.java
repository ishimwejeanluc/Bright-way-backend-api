package com.brightway.brightway_dropout.dto.school.response;

import com.brightway.brightway_dropout.enumeration.ESchoolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolResponseDTO {
    private UUID id;
    private String name;
    private String region;
    private String address;
    private ESchoolType type;
}
