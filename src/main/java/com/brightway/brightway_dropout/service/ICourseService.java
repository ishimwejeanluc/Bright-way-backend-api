package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.course.request.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.course.response.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseStatsResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import java.util.List;
import com.brightway.brightway_dropout.dto.student.response.StCourseOverviewDTO;
import java.util.UUID;

public interface ICourseService {
    List<StCourseOverviewDTO> getStudentCourseOverview(UUID studentId);
    CreateCourseResponseDTO createCourse(CreateCourseDTO createCourseDTO);
    CourseResponseDTO getCourseById(UUID id);
    List<CourseResponseDTO> getAllCourses();
    CourseResponseDTO updateCourse(UUID id, CreateCourseDTO updateDTO);
    DeleteResponseDTO deleteCourse(UUID id);
    CourseStatsResponseDTO getCourseStatsBySchool(UUID schoolId);
}