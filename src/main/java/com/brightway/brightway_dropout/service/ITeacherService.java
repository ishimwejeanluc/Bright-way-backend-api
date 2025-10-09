package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.teacher.request.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.teacher.response.*;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ITeacherService {
    CreateTeacherResponseDTO createTeacher(CreateTeacherDTO createTeacherDTO);
    TeacherDetailDTO getTeacherById(UUID id);
    List<TeacherResponseDTO> getAllTeachers();
    TeacherResponseDTO updateTeacher(UUID id, CreateTeacherDTO updateTeacherDTO);
    DeleteResponseDTO deleteTeacher(UUID id);
    TeacherStatsResponseDTO getTeacherStatsBySchool(UUID schoolId);
    TeacherDashboardStatsDTO getTeacherDashboardStats(UUID teacherId);
    TeacherCoursesStatsDTO getTeacherCoursesStats(UUID teacherId);
}
