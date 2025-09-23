package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.school.request.CreateSchoolDTO;
import com.brightway.brightway_dropout.dto.school.response.CreateSchoolResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.school.response.SchoolResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ISchoolService {
    CreateSchoolResponseDTO createSchool(CreateSchoolDTO createSchoolDTO);
    SchoolResponseDTO getSchoolById(UUID id);
    List<SchoolResponseDTO> getAllSchools();
    SchoolResponseDTO updateSchool(UUID id, CreateSchoolDTO updateSchoolDTO);
    DeleteResponseDTO deleteSchool(UUID id);
}
