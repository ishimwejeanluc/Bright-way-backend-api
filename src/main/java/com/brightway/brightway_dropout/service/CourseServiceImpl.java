package com.brightway.brightway_dropout.service;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;

import com.brightway.brightway_dropout.dto.course.request.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.course.response.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseDetailDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseStatsResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.UUID;
import com.brightway.brightway_dropout.dto.student.response.StCourseOverviewDTO;
import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.dto.student.response.StCourseMarkDTO;

import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.Course;
import com.brightway.brightway_dropout.model.DropoutPrediction;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.repository.ICourseRepository;
import com.brightway.brightway_dropout.repository.IEnrollmentRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements ICourseService {
    private final IGradeRepository gradeRepository;
    private final ICourseRepository courseRepository;
    private final ISchoolRepository schoolRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final JwtUtil jwtUtil;

    @Override
    public List<StCourseOverviewDTO> getStudentCourseOverview(UUID studentId) {
        // Fetch all enrollments for the student
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        if (enrollments == null || enrollments.isEmpty()) {
            throw new ResourceNotFoundException("No enrolled courses found for student with ID " + studentId);
        }

        // Fetch all marks for the student (grouped by course name)
        List<Object[]> rows = gradeRepository.findAllCourseMarksForStudent(studentId);
        Map<String, List<StCourseMarkDTO>> courseMarksMap = new LinkedHashMap<>();
        if (rows != null) {
            for (Object[] row : rows) {
                String courseName = row[0] != null ? row[0].toString() : "";
                String type = row[1] != null ? row[1].toString() : "";
                String title = row[2] != null ? row[2].toString() : "";
                Double score = row[3] != null ? Double.parseDouble(row[3].toString()) : null;
                StCourseMarkDTO markDTO = new StCourseMarkDTO(type, title, score);
                courseMarksMap.computeIfAbsent(courseName, k -> new ArrayList<>()).add(markDTO);
            }
        }

        // Build overview for all enrolled courses
        List<StCourseOverviewDTO> overviewList = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            String courseName = course != null ? course.getName() : "";
            List<StCourseMarkDTO> marks = courseMarksMap.getOrDefault(courseName, new ArrayList<>());
            List<Double> scores = marks.stream()
                .map(StCourseMarkDTO::getScore)
                .filter(java.util.Objects::nonNull)
                .toList();
            Double currentGpa = scores.isEmpty() ? 0.0 : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            overviewList.add(new StCourseOverviewDTO(courseName, currentGpa, marks));
        }
        return overviewList;
    }
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
            .map(course -> {
                int enrollmentCount = course.getEnrollments() != null ? course.getEnrollments().size() : 0;
                // Count at-risk students using repository method to avoid lazy loading
                int atRiskStudents = courseRepository.countAtRiskStudentsByCourseId(course.getId());
                return new CourseDetailDTO(
                    course.getName(),
                    course.getGrade(),
                    course.getCredits(),
                    course.getTeacher() != null ? course.getTeacher().getUser().getName() : null,
                    course.isActive(),
                    enrollmentCount,
                    atRiskStudents
                );
            })
            .toList();
        return new CourseStatsResponseDTO(totalCourses, totalActiveCourses, totalInactiveCourses, courseDetails);
    }
}