package com.brightway.brightway_dropout.dto.school.request;

import com.brightway.brightway_dropout.enumeration.ESchoolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSchoolDTO {
    @NotBlank(message = "School name is required")
    private String name;
    @NotBlank(message = "Region is required")
    private String region;
    @NotBlank(message = "Address is required")
    private String address;
    @NotNull(message = "School type is required")
    private ESchoolType type;
    @NotNull(message = "Principal details are required")
    private PrincipalDTO principal;
}
