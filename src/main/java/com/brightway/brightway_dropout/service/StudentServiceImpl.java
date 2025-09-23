package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.student.response.StudentDetailDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentStatsResponseDTO;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements IStudentService {
    private final IStudentRepository studentRepository;

    @Override
    public StudentStatsResponseDTO getStudentStatsBySchool(UUID schoolId) {
        List<Student> students = studentRepository.findBySchoolId(schoolId);
        int totalStudents = students.size();
        int totalCriticalRisk = (int) students.stream()
            .flatMap(s -> s.getDropoutPredictions() != null ? s.getDropoutPredictions().stream() : java.util.stream.Stream.empty())
            .filter(dp -> dp.getRiskLevel() == ERiskLevel.CRITICAL)
            .count();
        double totalAttendancePercentage = calculateTodayAttendancePercentage(students);
        int totalCourses = students.stream().flatMap(s -> s.getEnrollments().stream()).map(e -> e.getCourse()).collect(Collectors.toSet()).size();
        List<StudentDetailDTO> studentDetails = students.stream().map(s -> new StudentDetailDTO(
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
        return totalMarks / grades.size(); // Simple average, adjust if you have GPA logic
    }

    private static String getLatestRiskLevel(Student student) {
        if (student.getDropoutPredictions() == null || student.getDropoutPredictions().isEmpty()) return null;
        return student.getDropoutPredictions().get(student.getDropoutPredictions().size() - 1).getRiskLevel().name();
    }
}
