package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.student.response.StudentStatsResponseDTO;
import java.util.UUID;

public interface IStudentService {
    StudentStatsResponseDTO getStudentStatsBySchool(UUID schoolId);
}
