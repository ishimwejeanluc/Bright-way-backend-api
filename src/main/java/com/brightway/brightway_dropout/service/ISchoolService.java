package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.CreateSchoolDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateSchoolResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.SchoolResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ISchoolService {
    CreateSchoolResponseDTO createSchool(CreateSchoolDTO createSchoolDTO);
    SchoolResponseDTO getSchoolById(UUID id);
    List<SchoolResponseDTO> getAllSchools();
    DeleteResponseDTO deleteSchool(UUID id);
}
