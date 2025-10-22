


package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.teacher.request.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.teacher.response.*;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseStatsDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseWeeklyAttendanceTrendDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.school.response.SchoolResponseDTO;
import com.brightway.brightway_dropout.enumeration.EAttendanceStatus;
import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.enumeration.EUserRole;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;
import com.brightway.brightway_dropout.model.Attendance;
import com.brightway.brightway_dropout.model.Course;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.model.Student;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        List<Teacher> teachers = teacherRepository.findAllBySchoolIdWithCourses(schoolId);

        List<TeacherDetailDTO> teacherDetails = teachers.stream().map(t -> {
            String name = t.getUser() != null ? t.getUser().getName() : null;
            String specialization = t.getSpecialization();
            List<String> courses = t.getCourses() != null ?
                t.getCourses().stream().map(c -> c.getName()).toList() : List.of();

            return new TeacherDetailDTO(name, specialization, courses, t.isActive());
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

    @Override
    public TeacherDetailDTO getTeacherById(UUID id) {
        Teacher teacher = teacherRepository.findByIdWithCourses(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher with ID " + id + " not found"));

        String name = teacher.getUser() != null ? teacher.getUser().getName() : null;
        String specialization = teacher.getSpecialization();
        List<String> courses = teacher.getCourses() != null
            ? teacher.getCourses().stream().map(Course::getName).toList()
            : List.of();


        return new TeacherDetailDTO(name, specialization, courses, teacher.isActive());
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
    @Override
    @Transactional(readOnly = true)
    public TeacherDashboardStatsDTO getTeacherDashboardStats(UUID userId) {
        UUID teacherId = getTeacherId(userId);
        Teacher teacher = teacherRepository.findByIdWithCourses(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher with ID " + teacherId + " not found"));

        // Total courses
        int totalCourses = teacher.getCourses() != null ? teacher.getCourses().size() : 0;
        Set<Student> students = new HashSet<>();
        int atRiskStudents = 0;
        if (teacher.getCourses() != null) {
            for (Course course : teacher.getCourses()) {
                if (course.getEnrollments() != null) {
                    for (Enrollment enrollment : course.getEnrollments()) {
                        Student student = enrollment.getStudent();
                        if (student != null) {
                            students.add(student);
                            if (student.getDropoutPredictions() != null &&
                                student.getDropoutPredictions().stream().anyMatch(dp -> dp.getRiskLevel() == ERiskLevel.HIGH)) {
                                atRiskStudents++;
                            }
                        }
                    }
                }
            }
        }
        int totalStudents = students.size();
        LocalDate today = LocalDate.now();
        int totalAttendanceRecords = 0;
        int presentCount = 0;
        for (Student student : students) {
            if (student.getAttendanceRecords() != null) {
                for (Attendance attendance : student.getAttendanceRecords()) {
                    if (attendance.getDate().equals(today)) {
                        totalAttendanceRecords++;
                        if (attendance.getStatus() == EAttendanceStatus.PRESENT) {
                            presentCount++;
                        }
                    }
                }
            }
        }
        double todayAttendancePercentage = totalAttendanceRecords > 0
                ? (presentCount * 100.0) / totalAttendanceRecords
                : 0.0;
        return new TeacherDashboardStatsDTO(totalStudents, totalCourses, todayAttendancePercentage, atRiskStudents);
    }
    @Override
    @Transactional(readOnly = true)
    public TeacherCoursesStatsDTO getTeacherCoursesStats(UUID userId) {
        UUID teacherId = getTeacherId(userId);
        Teacher teacher = teacherRepository.findByIdWithCourses(teacherId)
            .orElseThrow(() -> new com.brightway.brightway_dropout.exception.ResourceNotFoundException("Teacher with ID " + teacherId + " not found"));

        List<Course> courses = teacher.getCourses();
        int totalCourses = courses != null ? courses.size() : 0;
        int overallStudents = 0;
        double totalAttendanceSum = 0;
        int attendanceCourseCount = 0;
        List<CourseStatsDTO> courseStatsList = new ArrayList<>();

        if (courses != null) {
            for (Course course : courses) {
                int courseTotalStudents = 0;
                int courseTodayAttendanceRecords = 0;
                int coursePresentCount = 0;
                boolean active = course.isActive();

                if (course.getEnrollments() != null) {
                    java.util.Set<Student> courseStudents = new java.util.HashSet<>();
                    for (Enrollment enrollment : course.getEnrollments()) {
                        Student student = enrollment.getStudent();
                        if (student != null) {
                            courseStudents.add(student);
                        }
                    }
                    courseTotalStudents = courseStudents.size();
                    overallStudents += courseTotalStudents;

                    // Attendance for today
                    for (Student student : courseStudents) {
                        if (student.getAttendanceRecords() != null) {
                            for (Attendance attendance : student.getAttendanceRecords()) {
                                if (attendance.getDate().equals(java.time.LocalDate.now())) {
                                    courseTodayAttendanceRecords++;
                                    if (attendance.getStatus() == EAttendanceStatus.PRESENT) {
                                        coursePresentCount++;
                                    }
                                }
                            }
                        }
                    }
                }
                double todayAttendancePercentage = courseTodayAttendanceRecords > 0 ? (coursePresentCount * 100.0) / courseTodayAttendanceRecords : 0.0;
                if (courseTodayAttendanceRecords > 0) {
                    totalAttendanceSum += todayAttendancePercentage;
                    attendanceCourseCount++;
                }
                courseStatsList.add(new CourseStatsDTO(
                    course.getName(),
                    courseTotalStudents,
                    todayAttendancePercentage,
                    active
                ));
            }
        }
        double averageAttendance = attendanceCourseCount > 0 ? totalAttendanceSum / attendanceCourseCount : 0.0;
        return new TeacherCoursesStatsDTO(
            totalCourses,
            overallStudents,
            averageAttendance,
            courseStatsList
        );
    }
    @Override
    @Transactional(readOnly = true)
    public TeacherAttendanceStatsDTO getTeacherAttendanceStats(UUID userId) {
        UUID teacherId = getTeacherId(userId);
        Teacher teacher = teacherRepository.findByIdWithCourses(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher with ID " + teacherId + " not found"));

        List<Course> courses = teacher.getCourses();
        int totalPresentToday = 0;
        int totalAbsentToday = 0;
        int allAttendanceRecords = 0;
        int presentCount = 0;
        List<CourseWeeklyAttendanceTrendDTO> weeklyTrends = new ArrayList<>();

        if (courses != null) {
            for (Course course : courses) {
                Set<Student> courseStudents = new HashSet<>();
                if (course.getEnrollments() != null) {
                    for (Enrollment enrollment : course.getEnrollments()) {
                        Student student = enrollment.getStudent();
                        if (student != null) {
                            courseStudents.add(student);
                        }
                    }
                }
                int coursePresentToday = 0;
                int courseAbsentToday = 0;
                List<Double> weeklyPercentages = new ArrayList<>();
                LocalDate today = LocalDate.now();
                for (Student student : courseStudents) {
                    if (student.getAttendanceRecords() != null) {
                        for (Attendance attendance : student.getAttendanceRecords()) {
                            if (attendance.getDate().equals(today)) {
                                if (attendance.getStatus() == EAttendanceStatus.PRESENT) {
                                    totalPresentToday++;
                                    coursePresentToday++;
                                } else if (attendance.getStatus() == EAttendanceStatus.ABSENT) {
                                    totalAbsentToday++;
                                    courseAbsentToday++;
                                }
                            }
                            allAttendanceRecords++;
                            if (attendance.getStatus() == EAttendanceStatus.PRESENT) {
                                presentCount++;
                            }
                        }
                    }
                }
                // Weekly attendance trend for this course (last 7 weeks)
                for (int week = 0; week < 7; week++) {
                    LocalDate weekStart = today.minusWeeks(week).with(DayOfWeek.MONDAY);
                    LocalDate weekEnd = weekStart.plusDays(6);
                    int weekPresent = 0;
                    int weekTotal = 0;
                    for (Student student : courseStudents) {
                        if (student.getAttendanceRecords() != null) {
                            for (Attendance attendance : student.getAttendanceRecords()) {
                                if (!attendance.getDate().isBefore(weekStart) && !attendance.getDate().isAfter(weekEnd)) {
                                    weekTotal++;
                                    if (attendance.getStatus() == EAttendanceStatus.PRESENT) {
                                        weekPresent++;
                                    }
                                }
                            }
                        }
                    }
                    double weekPercentage = weekTotal > 0 ? (weekPresent * 100.0) / weekTotal : 0.0;
                    weeklyPercentages.add(0, weekPercentage); 
                }
                weeklyTrends.add(new CourseWeeklyAttendanceTrendDTO(
                    course.getName(),
                    weeklyPercentages
                ));
            }
        }
        double overallAttendancePercentage = allAttendanceRecords > 0 ? (presentCount * 100.0) / allAttendanceRecords : 0.0;
        return new TeacherAttendanceStatsDTO(
            totalPresentToday,
            totalAbsentToday,
            overallAttendancePercentage,
            weeklyTrends
        );
    }
    public UUID getTeacherId(UUID userId){
    Teacher teacher = teacherRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return teacher.getId();
    }
}
