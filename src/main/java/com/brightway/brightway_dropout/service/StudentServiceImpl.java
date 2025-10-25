package com.brightway.brightway_dropout.service;
import com.brightway.brightway_dropout.enumeration.EStudentStatus;
import com.brightway.brightway_dropout.enumeration.EUserRole;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.dto.student.request.CreateStudentWithParentRequestDTO;
import com.brightway.brightway_dropout.dto.student.response.CreateStudentWithParentResponseDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentDetailDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentStatsResponseDTO;
import com.brightway.brightway_dropout.enumeration.EAttendanceStatus;
import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.Grade;
import com.brightway.brightway_dropout.model.Parent;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.repository.IAuthRepository;
import com.brightway.brightway_dropout.repository.IEnrollmentRepository;
import com.brightway.brightway_dropout.repository.IParentRepository;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import com.brightway.brightway_dropout.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements IStudentService {
    private final IStudentRepository studentRepository;
    private final IParentRepository parentRepository;
    private final IAuthRepository authRepository;
    private final ISchoolRepository schoolRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public StudentStatsResponseDTO getStudentStatsBySchool(UUID schoolId) {
        List<Student> students = studentRepository.findAllBySchoolIdWithEnrollments(schoolId);
        int totalStudents = students.size();
        int totalCriticalRisk = (int) students.stream()
            .flatMap(s -> s.getDropoutPredictions() != null ? s.getDropoutPredictions().stream() : java.util.stream.Stream.empty())
            .filter(dp -> dp.getRiskLevel() == ERiskLevel.CRITICAL)
            .count();
        double totalAttendancePercentage = calculateTodayAttendancePercentage(students);
        int totalCourses = students.stream().flatMap(s -> s.getEnrollments().stream()).map(e -> e.getCourse()).collect(Collectors.toSet()).size();
        List<StudentDetailDTO> studentDetails = students.stream().map(s -> new StudentDetailDTO(
                s.getId(),
                s.getUser() != null ? s.getUser().getName() : null,
                getLatestRiskLevel(s),
                calculateTodayAttendance(s),
                getGpaFromGrades(s)
        )).toList();
        return new StudentStatsResponseDTO(totalStudents, totalCriticalRisk, totalAttendancePercentage, totalCourses, studentDetails);
    }

    private static int calculateTodayAttendance(Student student) {
        LocalDate today = LocalDate.now();
        return (int) student.getAttendanceRecords().stream()
            .filter(a -> a.getDate() != null && a.getDate().isEqual(today) && a.getStatus() == EAttendanceStatus.PRESENT)
            .count();
    }

    private static double calculateTodayAttendancePercentage(List<Student> students) {
        if (students.isEmpty()) return 0.0;
        LocalDate today = LocalDate.now();
        long presentCount = students.stream()
            .flatMap(s -> s.getAttendanceRecords().stream())
            .filter(a -> a.getDate() != null && a.getDate().isEqual(today) && a.getStatus() == EAttendanceStatus.PRESENT)
            .count();
        long totalCount = students.stream()
            .flatMap(s -> s.getAttendanceRecords().stream())
            .filter(a -> a.getDate() != null && a.getDate().isEqual(today))
            .count();
        return totalCount == 0 ? 0.0 : (presentCount * 100.0) / totalCount;
    }

    private static double getGpaFromGrades(Student student) {
        List<Enrollment> enrollments = student.getEnrollments();
        if (enrollments == null || enrollments.isEmpty()) return 0.0;
        List<Grade> grades = enrollments.stream()
            .flatMap(e -> e.getGrades() != null ? e.getGrades().stream() : java.util.stream.Stream.empty())
            .toList();
        if (grades.isEmpty()) return 0.0;
        double totalMarks = grades.stream().mapToDouble(Grade::getMarks).sum();
        return totalMarks / grades.size(); 
    }

    private static String getLatestRiskLevel(Student student) {
        if (student.getDropoutPredictions() == null || student.getDropoutPredictions().isEmpty()) return null;
        return student.getDropoutPredictions().get(student.getDropoutPredictions().size() - 1).getRiskLevel().name();
    }

    @Override
    @Transactional
    public CreateStudentWithParentResponseDTO createStudentWithParent(CreateStudentWithParentRequestDTO dto) {
        // Check for existing parent user
        if (authRepository.findByEmail(dto.getParentEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Parent user with email " + dto.getParentEmail() + " already exists");
        }
        // Check for existing student user
        if (authRepository.findByEmail(dto.getStudentEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Student user with email " + dto.getStudentEmail() + " already exists");
        }
        // 1. Create Parent User
        User parentUser = new User();
        parentUser.setName(dto.getParentName());
        parentUser.setEmail(dto.getParentEmail());
        parentUser.setPassword(passwordEncoder.encode(dto.getParentPassword()));
        parentUser.setPhone(dto.getParentPhone());
        parentUser.setRole(EUserRole.PARENT);
        UUID currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId != null) {
            parentUser.setCreatedBy(currentUserId.toString());
            parentUser.setModifiedBy(currentUserId.toString());
        }
        User savedParentUser = authRepository.save(parentUser);

        // 2. Create Parent entity
        Parent parent = new Parent();
        parent.setUser(savedParentUser);
        parent.setOccupation(dto.getParentOccupation());
        Parent savedParent = parentRepository.save(parent);

        // 3. Create Student User
        User studentUser = new User();
        studentUser.setName(dto.getStudentName());
        studentUser.setEmail(dto.getStudentEmail());
        studentUser.setPassword(passwordEncoder.encode(dto.getStudentPassword()));
        studentUser.setPhone(dto.getStudentPhone());
        studentUser.setRole(EUserRole.STUDENT);
        if (currentUserId != null) {
            studentUser.setCreatedBy(currentUserId.toString());
            studentUser.setModifiedBy(currentUserId.toString());
        }
        User savedStudentUser = authRepository.save(studentUser);

        // 4. Get School
        School school = schoolRepository.findById(dto.getSchoolId())
            .orElseThrow(() -> new ResourceNotFoundException("School with ID " + dto.getSchoolId() + " not found"));

        // 5. Create Student entity
        Student student = new Student();
        student.setUser(savedStudentUser);
        student.setParent(savedParent);
        student.setSchool(school);
        student.setDateOfBirth(java.time.LocalDate.parse(dto.getDateOfBirth()));
        student.setGender(dto.getGender() != null ? com.brightway.brightway_dropout.enumeration.EGender.valueOf(dto.getGender()) : null);
        student.setEnrollmentYear(dto.getEnrollmentYear());
        student.setStatus(EStudentStatus.ACTIVE);
        if (currentUserId != null) {
            student.setCreatedBy(currentUserId.toString());
            student.setModifiedBy(currentUserId.toString());
        }
        Student savedStudent = studentRepository.save(student);

        // 6. Return response
        return new CreateStudentWithParentResponseDTO(
            savedStudent.getId(),
            savedParent.getId(),
            "Student and parent registered successfully"
        );
    }

    @Override
    public List<StudentDetailDTO> getStudentsByCourse(UUID courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        
        return enrollments.stream()
                .map(enrollment -> new StudentDetailDTO(
                    enrollment.getStudent().getId(),
                    enrollment.getStudent().getUser().getName()
                ))
                .collect(Collectors.toList());
    }
}
