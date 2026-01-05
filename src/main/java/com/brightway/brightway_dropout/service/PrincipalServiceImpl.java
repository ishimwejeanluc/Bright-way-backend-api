package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.principal.response.PrincipalDashboardOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.principal.response.PrincipalStudentOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.principal.response.StudentOverviewDTO;
import com.brightway.brightway_dropout.dto.principal.response.RiskLevelTrendDTO;
import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.model.*;
import com.brightway.brightway_dropout.repository.IAttendanceRepository;
import com.brightway.brightway_dropout.repository.IDropoutPredictionRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import com.brightway.brightway_dropout.repository.ITeacherRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrincipalServiceImpl implements IPrincipalService {
    private final IStudentRepository studentRepository;
    private final ITeacherRepository teacherRepository;
    private final IAttendanceRepository attendanceRepository;
    private final IDropoutPredictionRepository dropoutPredictionRepository;
    private final IGradeRepository gradeRepository;

    @Override
    public PrincipalDashboardOverviewResponseDTO getDashboardOverview(UUID schoolId) {
        int totalStudents = (int) studentRepository.countBySchoolId(schoolId);
        int totalTeachers = (int) teacherRepository.countBySchoolId(schoolId);

        // At-risk students: latest prediction per student, riskLevel HIGH or CRITICAL
        List<Student> students = studentRepository.findBySchoolId(schoolId);
        List<UUID> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
            Map<UUID, DropoutPrediction> latestPredictions = dropoutPredictionRepository.findLatestByStudentIds(studentIds)
                .stream().collect(Collectors.toMap(
                    d -> d.getStudent().getId(),
                    d -> d,
                    (d1, d2) -> {
                        LocalDateTime t1 = d1.getCreatedAt();
                        LocalDateTime t2 = d2.getCreatedAt();
                        if (t1 == null && t2 == null) return d1;
                        if (t1 == null) return d2;
                        if (t2 == null) return d1;
                        return t1.isAfter(t2) ? d1 : d2;
                    }
                ));
        int totalAtRiskStudents = (int) latestPredictions.values().stream()
            .filter(d -> d.getRiskLevel() == ERiskLevel.HIGH ||
                        d.getRiskLevel() == ERiskLevel.CRITICAL)
            .count();

        // Today's attendance: count present for today in this school
        LocalDate today = LocalDate.now();
            int todayAttendance = 0;
            Object[] kpis = attendanceRepository.findAttendanceKPIs(today, schoolId);
            if (kpis != null && kpis.length >= 2) {
                Number present = (Number) kpis[0];
                Number absent = (Number) kpis[1];
                int total = present.intValue() + absent.intValue();
                todayAttendance = total > 0 ? (int) Math.round((present.doubleValue() * 100.0) / total) : 0;
            }

        // Risk level trends: group by date, then by risk level
        List<DropoutPrediction> allPredictions = dropoutPredictionRepository.findAllBySchoolId(schoolId)
            .stream()
            .filter(d -> d.getCreatedAt() != null)
            .collect(Collectors.toList());
        Map<LocalDate, Map<ERiskLevel, Long>> trends = allPredictions.stream()
            .collect(Collectors.groupingBy(
                d -> d.getCreatedAt().toLocalDate(),
                Collectors.groupingBy(DropoutPrediction::getRiskLevel, Collectors.counting())
            ));
        List<RiskLevelTrendDTO> riskLevelTrends = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<ERiskLevel, Long>> entry : trends.entrySet()) {
            Map<ERiskLevel, Long> levels = entry.getValue();
            riskLevelTrends.add(new RiskLevelTrendDTO(
                entry.getKey().toString(),
                levels.getOrDefault(ERiskLevel.LOW, 0L).intValue(),
                levels.getOrDefault(ERiskLevel.MEDIUM, 0L).intValue(),
                levels.getOrDefault(ERiskLevel.HIGH, 0L).intValue(),
                levels.getOrDefault(ERiskLevel.CRITICAL, 0L).intValue()
            ));
        }

        return new PrincipalDashboardOverviewResponseDTO(
            totalStudents,
            totalTeachers,
            totalAtRiskStudents,
            todayAttendance,
            riskLevelTrends
        );
    }

    // Student Overview logic moved from PrincipalStudentOverviewServiceImpl
    public PrincipalStudentOverviewResponseDTO getStudentOverview(UUID schoolId) {
        List<Student> students = studentRepository.findAllBySchoolIdWithEnrollments(schoolId);
        int totalStudents = students.size();
        LocalDate today = LocalDate.now();
        int presentToday = 0;
        int atRisk = 0;
        List<StudentOverviewDTO> studentDTOs = new ArrayList<>();

        for (Student student : students) {
            // Attendance %: overall attendance rate for this student
            Double attendanceRate = attendanceRepository.findAttendanceRateForStudent(student.getId());
            int attendancePercent = attendanceRate != null ? attendanceRate.intValue() : 0;
            
            // GPA (average marks)
            List<Grade> grades = gradeRepository.findAllByStudentId(student.getId());
            double gpa = grades.stream().mapToDouble(Grade::getMarks).average().orElse(0.0);
            // Courses
            List<String> courses = student.getEnrollments() != null ?
                student.getEnrollments().stream().map(e -> e.getCourse().getName()).distinct().collect(Collectors.toList()) :
                Collections.emptyList();
            // Risk Level
            DropoutPrediction latestPrediction = dropoutPredictionRepository.findTopByStudentIdOrderByPredictedAtDesc(student.getId()).orElse(null);
            String riskLevel;
            if (latestPrediction != null && latestPrediction.getRiskLevel() != null) {
                riskLevel = latestPrediction.getRiskLevel().name();
            } else {
                riskLevel = "UNKNOWN";
            }
            if ("HIGH".equals(riskLevel) || "CRITICAL".equals(riskLevel)) {
                atRisk++;
            }
            studentDTOs.add(new StudentOverviewDTO(
                student.getId(),
                student.getUser() != null ? student.getUser().getName() : null,
                riskLevel,
                attendancePercent,
                gpa,
                courses
            ));
        }
        int todayAttendance = totalStudents > 0 ? (int) Math.round((presentToday * 100.0) / totalStudents) : 0;
        return new PrincipalStudentOverviewResponseDTO(
            totalStudents,
            atRisk,
            todayAttendance,
            studentDTOs
        );
    }
}
