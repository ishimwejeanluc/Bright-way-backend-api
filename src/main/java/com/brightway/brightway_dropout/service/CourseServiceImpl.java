package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.exception.TeacherNotFoundException;
import com.brightway.brightway_dropout.model.Course;
import com.brightway.brightway_dropout.repository.ICourseRepository;
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
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CreateCourseResponseDTO createCourse(CreateCourseDTO courseDTO) {
        try {
        

            // Create course
            Course course = new Course();
            course.setName(courseDTO.getName());
            course.setDescription(courseDTO.getDescription());

            // Set audit fields
            Long currentUserId = jwtUtil.getCurrentUserId();
            if (currentUserId != null) {
                course.setCreatedBy(currentUserId.toString());
                course.setModifiedBy(currentUserId.toString());
            }

            // Save course
            Course savedCourse = courseRepository.save(course);

            log.info("Course created successfully with ID: {}", savedCourse.getId());

            return new CreateCourseResponseDTO(
                    savedCourse.getId(),
                    savedCourse.getName(),
                    savedCourse.getDescription()
            );

        } catch (TeacherNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating course: {}", e.getMessage());
            throw new RuntimeException("Internal server error occurred during course creation", e);
        }
    }

}