package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.teacher.request.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.teacher.response.CreateTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.school.response.SchoolResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherStatsResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherDetailDTO;
import com.brightway.brightway_dropout.enumeration.EUserRole;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;
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

    public TeacherStatsResponseDTO getTeacherStatsBySchool(UUID schoolId) {
        List<Teacher> teachers = teacherRepository.findAll().stream()
            .filter(t -> t.getSchool() != null && t.getSchool().getId().equals(schoolId))
            .toList();

        List<TeacherDetailDTO> teacherDetails = teachers.stream().map(t -> {
            String name = t.getUser() != null ? t.getUser().getName() : null;
            String specialization = t.getSpecialization();
            List<String> courses = t.getCourses() != null ?
                t.getCourses().stream().map(c -> c.getName()).toList() : List.of();
            String status = t.getUser() != null && t.getUser().getRole() != null ? t.getUser().getRole().name() : null;
            return new TeacherDetailDTO(name, specialization, courses, status);
        }).toList();

        return new TeacherStatsResponseDTO(teachers.size(), teacherDetails);
    }
    @Override
    @Transactional
    public TeacherResponseDTO updateTeacher(UUID id, CreateTeacherDTO updateTeacherDTO) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher with ID " + id + " not found"));

        // Update user details
        User user = teacher.getUser();
        if (user != null) {
            user.setName(updateTeacherDTO.getName());
            user.setEmail(updateTeacherDTO.getEmail());
            if (updateTeacherDTO.getPassword() != null && !updateTeacherDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateTeacherDTO.getPassword()));
            }
            user.setPhone(updateTeacherDTO.getPhone());
            authRepository.save(user);
        }

        // Update school
        if (updateTeacherDTO.getSchoolId() != null) {
            School school = schoolRepository.findById(updateTeacherDTO.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School with ID " + updateTeacherDTO.getSchoolId() + " not found"));
            teacher.setSchool(school);
        }

        // Update specialization
        teacher.setSpecialization(updateTeacherDTO.getSpecialization());

        // Update courses
        if (updateTeacherDTO.getCourses() != null && !updateTeacherDTO.getCourses().isEmpty()) {
            List<UUID> courseIds = updateTeacherDTO.getCourses();
            List<Course> courses = courseRepository.findAllById(courseIds);
            courses.forEach(course -> course.setTeacher(teacher));
            courseRepository.saveAll(courses);
            teacher.setCourses(courses);
        }

        // Update audit field
        UUID currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId != null) {
            teacher.setModifiedBy(currentUserId.toString());
        }

        Teacher updatedTeacher = teacherRepository.save(teacher);
        return mapToTeacherResponseDTO(updatedTeacher);
    }
   

    @Override
    @Transactional
    public CreateTeacherResponseDTO createTeacher(CreateTeacherDTO createTeacherDTO) {
        // Check if user already exists
        Optional<User> existingUser = authRepository.findByEmail(createTeacherDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + createTeacherDTO.getEmail() + " already exists");
        }

        // Check if school exists
        School school = schoolRepository.findById(createTeacherDTO.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School with ID " + createTeacherDTO.getSchoolId() + " not found"));

        // Create user account first
        User newUser = new User();
        newUser.setName(createTeacherDTO.getName());
        newUser.setEmail(createTeacherDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(createTeacherDTO.getPassword()));
        newUser.setPhone(createTeacherDTO.getPhone());
        newUser.setRole(EUserRole.TEACHER);
        
        // Set audit fields for user
        UUID currentUserId = jwtUtil.getCurrentUserId();
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
            List<UUID> courseIds = createTeacherDTO.getCourses();
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
    }

    @Override
    public TeacherResponseDTO getTeacherById(UUID id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher with ID " + id + " not found"));
        return mapToTeacherResponseDTO(teacher);
    }

    @Override
    public List<TeacherResponseDTO> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teachers.stream()
                .map(this::mapToTeacherResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeleteResponseDTO deleteTeacher(UUID id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher with ID " + id + " not found"));
       
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
    }

    private TeacherResponseDTO mapToTeacherResponseDTO(Teacher teacher) {
        User user = teacher.getUser();
        School school = teacher.getSchool();

        SchoolResponseDTO schoolDTO = null;
        if (school != null) {
            schoolDTO = new SchoolResponseDTO(
                school.getId(),
                school.getName(),
                school.getRegion(),
                school.getAddress(),
                school.getType()
            );
        }

        List<CourseResponseDTO> courseDTOs = teacher.getCourses() != null ?
            teacher.getCourses().stream()
                .map(course -> new CourseResponseDTO(
                    course.getId(),
                    course.getName(),
                    course.getDescription()
                ))
                .toList() : List.of();

        return new TeacherResponseDTO(
            teacher.getId(),
            user != null ? user.getName() : null,
            user != null ? user.getEmail() : null,
            user != null ? user.getPhone() : null,
            teacher.getSpecialization(),
            schoolDTO,
            courseDTOs
        );
    }
}
