package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.teacher.request.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.teacher.response.CreateTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherStatsResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ITeacherService {
    CreateTeacherResponseDTO createTeacher(CreateTeacherDTO createTeacherDTO);
    TeacherResponseDTO getTeacherById(UUID id);
    List<TeacherResponseDTO> getAllTeachers();
    TeacherResponseDTO updateTeacher(UUID id, CreateTeacherDTO updateTeacherDTO);
    DeleteResponseDTO deleteTeacher(UUID id);
    TeacherStatsResponseDTO getTeacherStatsBySchool(UUID schoolId);
}
