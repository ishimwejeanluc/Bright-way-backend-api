package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChronicAbsenceDTO {
    private List<StudentAbsenceDTO> mostConsecutive;
    private List<StudentAbsenceDTO> mostTotal;
}