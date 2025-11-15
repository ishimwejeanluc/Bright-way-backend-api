package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.attendance.request.AttendanceRequestDTO;
import com.brightway.brightway_dropout.dto.attendance.response.*;
import com.brightway.brightway_dropout.dto.student.response.StAttendanceOverviewDTO;
import com.brightway.brightway_dropout.enumeration.EAttendanceStatus;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.Attendance;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.repository.IAttendanceRepository;
import com.brightway.brightway_dropout.repository.IEnrollmentRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.brightway.brightway_dropout.util.TypeSafeUtils;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService {
    
    private final IAttendanceRepository attendanceRepository;
    private final IStudentRepository studentRepository;
    private final IGradeRepository gradeRepository;
    private final IEnrollmentRepository enrollmentRepository;
   
    @Override
    @Transactional(readOnly = true)
    public StAttendanceOverviewDTO getStudentAttendanceOverview(UUID studentId) {
        // Overall attendance percentage
        Double overallAttendance = attendanceRepository.findAttendanceRateForStudent(studentId);
        if (overallAttendance == null) overallAttendance = 0.0;

        // Weekly attendance percentages (last 4 weeks)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.Map<String, Double> weeklyAttendance = new java.util.LinkedHashMap<>();
        for (int i = 0; i < 4; i++) {
            java.time.LocalDate startOfWeek = today.minusWeeks(i).with(java.time.DayOfWeek.MONDAY);
            java.time.LocalDate endOfWeek = startOfWeek.plusDays(6);
            Double weekAttendance = attendanceRepository.findAttendanceRateForStudentInPeriod(studentId, startOfWeek, endOfWeek);
            if (weekAttendance == null) weekAttendance = 0.0;
            String label = "Week " + (4 - i) + " (" + startOfWeek + ")";
            weeklyAttendance.put(label, weekAttendance);
        }

        // Per-course attendance percentages
        java.util.Map<String, Double> courseAttendance = new java.util.LinkedHashMap<>();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        for (Enrollment enrollment : enrollments) {
            UUID courseId = enrollment.getCourse().getId();
            String courseName = enrollment.getCourse().getName();
            Double courseAttendanceRate = attendanceRepository.findAttendanceRateForStudentInCourse(studentId, courseId);
            if (courseAttendanceRate == null) courseAttendanceRate = 0.0;
            courseAttendance.put(courseName, courseAttendanceRate);
        }

        return new com.brightway.brightway_dropout.dto.student.response.StAttendanceOverviewDTO(
            overallAttendance,
            weeklyAttendance,
            courseAttendance
        );
    }
    
    @Override
    @Transactional
    public AttendanceResponseDTO saveBulkAttendance(AttendanceRequestDTO requestDTO) {
        LocalDate currentDate = LocalDate.now();
        UUID courseId = requestDTO.getCourseId();
        List<UUID> presentStudentIds = requestDTO.getPresentStudentIds();
        List<UUID> absentStudentIds = requestDTO.getAbsentStudentIds();
        
        // Check if attendance has already been taken for this course today
        List<Attendance> existingAttendanceForCourse = attendanceRepository.findByDateAndCourseId(currentDate, courseId);
        if (!existingAttendanceForCourse.isEmpty()) {
            throw new ResourceAlreadyExistsException("Attendance has already been taken for this course today");
        }
        
        // Combine all student IDs
        List<UUID> allStudentIds = new ArrayList<>();
        if (presentStudentIds != null) {
            allStudentIds.addAll(presentStudentIds);
        }
        if (absentStudentIds != null) {
            allStudentIds.addAll(absentStudentIds);
        }
        
        // Validate that students exist
        List<Student> students = studentRepository.findAllById(allStudentIds);
        if (students.size() != allStudentIds.size()) {
            throw new ResourceNotFoundException("One or more students not found");
        }
        
        // Create a map for quick student lookup
        Map<UUID, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));
        
        List<Attendance> attendanceToSave = new ArrayList<>();
        
        // Process present students
        if (presentStudentIds != null) {
            for (UUID studentId : presentStudentIds) {
                Attendance attendance = new Attendance();
                attendance.setStudent(studentMap.get(studentId));
                attendance.setDate(currentDate);
                attendance.setStatus(EAttendanceStatus.PRESENT);
                attendanceToSave.add(attendance);
            }
        }
        
        // Process absent students
        if (absentStudentIds != null) {
            for (UUID studentId : absentStudentIds) {
                Attendance attendance = new Attendance();
                attendance.setStudent(studentMap.get(studentId));
                attendance.setDate(currentDate);
                attendance.setStatus(EAttendanceStatus.ABSENT);
                attendanceToSave.add(attendance);
            }
        }
        
        // Save all attendance records
        attendanceRepository.saveAll(attendanceToSave);
        
        int totalPresent = presentStudentIds != null ? presentStudentIds.size() : 0;
        int totalAbsent = absentStudentIds != null ? absentStudentIds.size() : 0;
        
        return new AttendanceResponseDTO(String.format("Attendance saved successfully for %s - %d present, %d absent", 
                currentDate, totalPresent, totalAbsent));
    }
    
    @Override
    @Transactional(readOnly = true)
    public AttendanceOverviewResponseDTO getAttendanceOverview(UUID schoolId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(6); // Last 7 days including today
        
        // Get simple KPIs for today - returns Object[presentToday, absentToday]
    Object[] kpiData = attendanceRepository.findAttendanceKPIs(today, schoolId);
    System.out.println("DEBUG kpiData: " + java.util.Arrays.deepToString(kpiData));
        
        // Unpack nested array for KPIs
        Object[] kpiStats = kpiData.length > 0 && kpiData[0] != null ? (Object[]) kpiData[0] : new Object[2];
        AttendanceKPIsDTO kpisDTO = new AttendanceKPIsDTO(
            kpiStats.length > 0 ? TypeSafeUtils.safeToInteger(kpiStats, 0) : 0,
            kpiStats.length > 1 ? TypeSafeUtils.safeToInteger(kpiStats, 1) : 0
        );
        
        // Get daily attendance stats from database
        List<Object[]> dailyStatsData = attendanceRepository.findDailyAttendanceStats(startOfWeek, today, schoolId);
        Map<LocalDate, Double> attendanceMap = dailyStatsData.stream()
                .collect(Collectors.toMap(
                    data -> safeToLocalDate(data, 1),      // date - using safe method
                    data -> TypeSafeUtils.safeToDouble(data, 2)          // attendance
                ));
        
        // Generate 7 days including weekends/future dates with 0.0
        List<DailyAttendanceStatsDTO> dailyStats = new ArrayList<>();
        String[] dayAbbreviations = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            String dayAbbrev = dayAbbreviations[date.getDayOfWeek().getValue() - 1];
            Double attendance = attendanceMap.getOrDefault(date, 0.0);
            
            dailyStats.add(new DailyAttendanceStatsDTO(dayAbbrev, attendance, date));
        }
        
        // Calculate weekly averages
        Double weeklyAttendanceRate = attendanceRepository.findAttendanceRateForPeriod(startOfWeek, today, schoolId);
        Integer totalAbsences = attendanceRepository.findTotalAbsencesForPeriod(startOfWeek, today, schoolId);
        
        WeeklyAveragesDTO weeklyAverages = new WeeklyAveragesDTO(
            weeklyAttendanceRate != null ? weeklyAttendanceRate : 0.0,
            totalAbsences != null ? totalAbsences : 0
        );
        WeeklyAttendanceTrendsDTO weeklyTrends = new WeeklyAttendanceTrendsDTO(dailyStats, weeklyAverages);
        
        // Get subject performance - returns List<Object[subjectName, currentAverage]>
        List<Object[]> subjectData = gradeRepository.findSubjectPerformance(schoolId);
        List<SubjectPerformanceDTO> subjectPerformance = subjectData.stream()
                .map(data -> new SubjectPerformanceDTO(
                    data.length > 0 ? TypeSafeUtils.safeToString(data, 0) : "",
                    data.length > 1 ? TypeSafeUtils.safeToDouble(data, 1) : 0.0
                ))
                .collect(Collectors.toList());
        
        // Get overall stats - returns Object[averageGPA, highestScore, lowestScore]
        Object[] overallData = gradeRepository.findOverallStats(schoolId);
       System.out.println("DEBUG overallData: " + java.util.Arrays.deepToString(overallData));
            Object[] stats = overallData.length > 0 && overallData[0] != null ? (Object[]) overallData[0] : new Object[3];
            OverallStatsDTO overallStats = new OverallStatsDTO(
                stats.length > 0 ? convertToDouble(stats[0]) : 0.0,
                stats.length > 1 ? convertToDouble(stats[1]) : 0.0,
                stats.length > 2 ? convertToDouble(stats[2]) : 0.0
            );
    
    PerformanceTrendsDTO performanceTrends = new PerformanceTrendsDTO(subjectPerformance, overallStats);
        
        return new AttendanceOverviewResponseDTO(kpisDTO, weeklyTrends, performanceTrends);
    }
    
    // Helper methods for safe casting
    
    private LocalDate safeToLocalDate(Object[] array, int index) {
        if (array == null || index >= array.length || array[index] == null) {
            return LocalDate.now();
        }
        Object value = array[index];
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
     
    
    // Helper for BigDecimal/Number conversion
    private double convertToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}