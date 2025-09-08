package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.TeacherResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ITeacherService {
    CreateTeacherResponseDTO createTeacher(CreateTeacherDTO createTeacherDTO);
    TeacherResponseDTO getTeacherById(UUID id);
    List<TeacherResponseDTO> getAllTeachers();
    DeleteResponseDTO deleteTeacher(UUID id);
}
