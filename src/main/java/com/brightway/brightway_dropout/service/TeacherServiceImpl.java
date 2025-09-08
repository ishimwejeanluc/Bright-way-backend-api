package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.SchoolResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.TeacherResponseDTO;
import com.brightway.brightway_dropout.enumeration.EUserRole;
import com.brightway.brightway_dropout.exception.SchoolNotFoundException;
import com.brightway.brightway_dropout.exception.UserAlreadyExistsException;
import com.brightway.brightway_dropout.model.Course;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.model.Teacher;
import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.repository.IAuthRepository;
import com.brightway.brightway_dropout.repository.ICourseRepository;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.repository.ITeacherRepository;
import com.brightway.brightway_dropout.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements ITeacherService {
    private final ITeacherRepository teacherRepository;
    private final IAuthRepository authRepository;
    private final ISchoolRepository schoolRepository;
    private final ICourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CreateTeacherResponseDTO createTeacher(CreateTeacherDTO createTeacherDTO) {
        try {
            // Check if user already exists
            Optional<User> existingUser = authRepository.findByEmail(createTeacherDTO.getEmail());
            if (existingUser.isPresent()) {
                throw new UserAlreadyExistsException("User with email " + createTeacherDTO.getEmail() + " already exists");
            }

            // Check if school exists
            School school = schoolRepository.findById(createTeacherDTO.getSchoolId())
                    .orElseThrow(() -> new SchoolNotFoundException("School with ID " + createTeacherDTO.getSchoolId() + " not found"));

            // Create user account first
            User newUser = new User();
            newUser.setName(createTeacherDTO.getName());
            newUser.setEmail(createTeacherDTO.getEmail());
            newUser.setPassword(passwordEncoder.encode(createTeacherDTO.getPassword()));
            newUser.setPhone(createTeacherDTO.getPhone());
            newUser.setRole(EUserRole.TEACHER);
            
            // Set audit fields for user
            Long currentUserId = jwtUtil.getCurrentUserId();
            if (currentUserId != null) {
                newUser.setCreatedBy(currentUserId.toString());
                newUser.setModifiedBy(currentUserId.toString());
            }

            // Save user
            User savedUser = authRepository.save(newUser);

            // Create teacher profile
            Teacher newTeacher = new Teacher();
            newTeacher.setUser(savedUser);
            newTeacher.setSchool(school);
            newTeacher.setSpecialization(createTeacherDTO.getSpecialization());
            
            // Set audit fields for teacher
            if (currentUserId != null) {
                newTeacher.setCreatedBy(currentUserId.toString());
                newTeacher.setModifiedBy(currentUserId.toString());
            }

            // Save teacher first to get the generated ID
            Teacher savedTeacher = teacherRepository.save(newTeacher);

            // Update courses with the teacher ID (Step 2)
            if (createTeacherDTO.getCourses() != null && !createTeacherDTO.getCourses().isEmpty()) {
                List<UUID> courseIds = createTeacherDTO.getCourses().stream()
                        .map(Course::getId)
                        .collect(Collectors.toList());
                
                // Get courses by IDs and assign teacher
                List<Course> courses = courseRepository.findAllById(courseIds);
                courses.forEach(course -> course.setTeacher(savedTeacher));
                
                // Save updated courses
                courseRepository.saveAll(courses);
            }

            // Return response DTO
            return new CreateTeacherResponseDTO(
                    savedTeacher.getId(),
                    savedUser.getId()
            );

        } catch (UserAlreadyExistsException | SchoolNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred during teacher creation", e);
        }
    }

    @Override
    public TeacherResponseDTO getTeacherById(UUID id) {
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Teacher with ID " + id + " not found"));

            return mapToTeacherResponseDTO(teacher);

        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred while fetching teacher", e);
        }
    }

    @Override
    public List<TeacherResponseDTO> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherRepository.findAll();
            
            return teachers.stream()
                    .map(this::mapToTeacherResponseDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred while fetching teachers", e);
        }
    }

    @Override
    @Transactional
    public DeleteResponseDTO deleteTeacher(UUID id) {
        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Teacher with ID " + id + " not found"));

            // Remove teacher association from courses
            if (teacher.getCourses() != null && !teacher.getCourses().isEmpty()) {
                teacher.getCourses().forEach(course -> course.setTeacher(null));
                courseRepository.saveAll(teacher.getCourses());
            }

            // Delete teacher and associated user
            User user = teacher.getUser();
            teacherRepository.delete(teacher);
            if (user != null) {
                authRepository.delete(user);
            }

            return new DeleteResponseDTO("Teacher deleted successfully");

        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred while deleting teacher", e);
        }
    }

    private TeacherResponseDTO mapToTeacherResponseDTO(Teacher teacher) {
        User user = teacher.getUser();
        School school = teacher.getSchool();
        
        // Map school
        SchoolResponseDTO schoolDTO = new SchoolResponseDTO(
                school.getId(),
                school.getName(),
                school.getRegion(),
                school.getAddress(),
                school.getType()
        );

        // Map courses
        List<CourseResponseDTO> courseDTOs = teacher.getCourses() != null ? 
                teacher.getCourses().stream()
                        .map(course -> new CourseResponseDTO(
                                course.getId(),
                                course.getName(),
                                course.getDescription()
                        ))
                        .collect(Collectors.toList()) : 
                List.of();

        return new TeacherResponseDTO(
                teacher.getId(),
                user.getName()

        );
    }
}
