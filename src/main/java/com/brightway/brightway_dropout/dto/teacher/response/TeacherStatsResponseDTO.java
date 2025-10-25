package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherStatsResponseDTO {
    private int totalTeachers;
    private List<TeacherDetailDTO> teachers;
}
