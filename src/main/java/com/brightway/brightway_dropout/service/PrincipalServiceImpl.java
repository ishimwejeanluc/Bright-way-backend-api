package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.principal.response.PrincipalDashboardOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.principal.response.PrincipalStudentOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.principal.response.PrincipalStudentProfileDTO;
import com.brightway.brightway_dropout.dto.principal.response.StudentOverviewDTO;
import com.brightway.brightway_dropout.dto.principal.response.RiskLevelTrendDTO;
import com.brightway.brightway_dropout.dto.student.response.StCourseMarkDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.StBehaviorIncidentDTO;
import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.*;
import com.brightway.brightway_dropout.repository.IAttendanceRepository;
import com.brightway.brightway_dropout.repository.IDropoutPredictionRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import com.brightway.brightway_dropout.repository.ITeacherRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;
import com.brightway.brightway_dropout.repository.IBehaviorIncidentRepository;

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
    private final IBehaviorIncidentRepository behaviorIncidentRepository;

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

        // Risk level trends: get latest prediction per student per date, then count by risk level
        List<Object[]> trendResults = dropoutPredictionRepository.findRiskLevelTrendsBySchool(schoolId);
        
        // Group results by date
        Map<String, RiskLevelTrendDTO> trendMap = new HashMap<>();
        
        for (Object[] row : trendResults) {
            if (row[0] == null || row[1] == null) {
                continue; // Skip null dates or risk levels
            }
            
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            String date = sqlDate.toLocalDate().toString();
            String riskLevel = (String) row[1];
            int count = ((Number) row[2]).intValue();
            
            RiskLevelTrendDTO dto = trendMap.getOrDefault(date, 
                new RiskLevelTrendDTO(date, 0, 0, 0, 0));
            
            switch (riskLevel) {
                case "LOW" -> dto.setLow(count);
                case "MEDIUM" -> dto.setMedium(count);
                case "HIGH" -> dto.setHigh(count);
                case "CRITICAL" -> dto.setCritical(count);
            }
            
            trendMap.put(date, dto);
        }
        
        List<RiskLevelTrendDTO> riskLevelTrends = new ArrayList<>(trendMap.values());

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
    
    @Override
    public PrincipalStudentProfileDTO getStudentProfile(UUID studentId) {
        // Fetch basic student profile data
        List<Object[]> results = studentRepository.findStudentProfileById(studentId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Student with ID " + studentId + " not found");
        }
        
        Object[] profileData = results.get(0);
        
        // Map basic data from query
        UUID id = profileData[0] instanceof UUID ? (UUID) profileData[0] : UUID.fromString(profileData[0].toString());
        String name = (String) profileData[1];
        String studentCode = (String) profileData[2];
        String schoolName = (String) profileData[3];
        String parentName = (String) profileData[4];
        String parentPhone = (String) profileData[5];
        String parentEmail = (String) profileData[6];
        String parentOccupation = (String) profileData[7];
        String riskLevel = profileData[8] != null ? profileData[8].toString() : "UNKNOWN";
        Double dropoutProbability = profileData[9] != null ? ((Number) profileData[9]).doubleValue() : 0.0;
        Integer avgAttendance = profileData[10] != null ? ((Number) profileData[10]).intValue() : 0;
        Double academicScore = profileData[11] != null ? ((Number) profileData[11]).doubleValue() : 0.0;
        
        // Calculate engagement percentage (simple formula based on attendance and academic performance)
        Integer engagementPercent = (int) Math.round((avgAttendance + (academicScore * 10)) / 2);
        
        // Get current grades (average per course)
        List<Object[]> gradeData = gradeRepository.findRecentGradesForStudent(studentId);
        List<StCourseMarkDTO> currentGrades = gradeData.stream()
            .map(row -> new StCourseMarkDTO(
                "Average",        // type
                (String) row[0],  // title (course name)
                row[1] != null ? ((Number) row[1]).doubleValue() : 0.0  // score (average marks)
            ))
            .collect(Collectors.toList());
        
        // Get intervention log (behavior incidents)
        List<Object[]> incidentData = behaviorIncidentRepository.findRecentIncidentsForStudent(studentId);
        List<StBehaviorIncidentDTO> interventionLog = incidentData.stream()
            .map(row -> new StBehaviorIncidentDTO(
                (String) row[0],  // note
                (String) row[1],  // incidentType
                (String) row[2]   // severityLevel
            ))
            .collect(Collectors.toList());
        
        return new PrincipalStudentProfileDTO(
            id,
            name,
            studentCode,
            schoolName,
            parentName,
            parentPhone,
            parentEmail,
            parentOccupation,
            riskLevel,
            dropoutProbability,
            avgAttendance,
            academicScore,
            engagementPercent,
            currentGrades,
            interventionLog
        );
    }
}
