package com.brightway.brightway_dropout.service;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;

import com.brightway.brightway_dropout.dto.course.request.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.course.response.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseDetailDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseStatsResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import java.util.List;
import java.util.UUID;

import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.Course;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.ICourseRepository;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements ICourseService {
    private final ICourseRepository courseRepository;
    private final ISchoolRepository schoolRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CreateCourseResponseDTO createCourse(CreateCourseDTO courseDTO) {
        if (courseRepository.findByName(courseDTO.getName()).isPresent()) {
        throw new ResourceAlreadyExistsException("Course with name '" + courseDTO.getName() + "' already exists");
        }

        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setGrade(courseDTO.getGrade());
        course.setCredits(courseDTO.getCredits());
        School school = new School();
        if(!schoolRepository.findById(courseDTO.getSchoolId()).isPresent()) {
            throw new ResourceNotFoundException("School with id '" + courseDTO.getSchoolId() + "' not found");
        }
        school.setId(courseDTO.getSchoolId());
        course.setSchool(school);
        UUID currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId != null) {
            course.setCreatedBy(currentUserId.toString());
            course.setModifiedBy(currentUserId.toString());
        }
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());
        return new CreateCourseResponseDTO(
                savedCourse.getId(),
                savedCourse.getName()
        );
    }

    @Override
    public CourseResponseDTO getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + id + " not found"));
        return new CourseResponseDTO(course.getId(), course.getName(), course.getDescription());
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(c -> new CourseResponseDTO(c.getId(), c.getName(), c.getDescription()))
                .toList();
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourse(UUID id, CreateCourseDTO updateDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + id + " not found"));
        
        courseRepository.findByName(updateDTO.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResourceAlreadyExistsException("Course with name '" + updateDTO.getName() + "' already exists");
                });
        course.setName(updateDTO.getName());
        course.setDescription(updateDTO.getDescription());
        Course updated = courseRepository.save(course);
        return new CourseResponseDTO(updated.getId(), updated.getName(), updated.getDescription());
    }

    @Override
    @Transactional
    public DeleteResponseDTO deleteCourse(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + id + " not found"));
        courseRepository.delete(course);
        return new DeleteResponseDTO("Course deleted successfully");
    }

    @Override
    public CourseStatsResponseDTO getCourseStatsBySchool(UUID schoolId) {
        List<Course> courses = courseRepository.findAllBySchoolIdWithDetails(schoolId);
        int totalCourses = courses.size();
        int totalActiveCourses = (int) courses.stream().filter(Course::isActive).count();
        int totalInactiveCourses = totalCourses - totalActiveCourses;
        List<CourseDetailDTO> courseDetails = courses.stream()
            .map(course -> new CourseDetailDTO(
                course.getName(),
                course.getGrade(),
                course.getCredits(),
                course.getTeacher() != null ? course.getTeacher().getUser().getName() : null,
                course.isActive(),
                course.getEnrollments() != null ? course.getEnrollments().size() : 0
            ))
            .toList();
        return new CourseStatsResponseDTO(totalCourses, totalActiveCourses, totalInactiveCourses, courseDetails);
    }
}