package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.course.request.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.course.response.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseStatsResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import java.util.List;
import java.util.UUID;

public interface ICourseService {
    CreateCourseResponseDTO createCourse(CreateCourseDTO createCourseDTO);
    CourseResponseDTO getCourseById(UUID id);
    List<CourseResponseDTO> getAllCourses();
    CourseResponseDTO updateCourse(UUID id, CreateCourseDTO updateDTO);
    DeleteResponseDTO deleteCourse(UUID id);
    List<CourseStatsResponseDTO> getCourseStatsBySchool(UUID schoolId);
}