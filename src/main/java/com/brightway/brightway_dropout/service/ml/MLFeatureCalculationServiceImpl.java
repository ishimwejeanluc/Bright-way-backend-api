package com.brightway.brightway_dropout.service.ml;

import com.brightway.brightway_dropout.dto.ml.StudentFeaturesDTO;
import com.brightway.brightway_dropout.enumeration.EAttendanceStatus;
import com.brightway.brightway_dropout.enumeration.EGender;
import com.brightway.brightway_dropout.enumeration.ESeverityLevel;
import com.brightway.brightway_dropout.model.Attendance;
import com.brightway.brightway_dropout.model.BehaviorIncident;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.Grade;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.repository.IAttendanceRepository;
import com.brightway.brightway_dropout.repository.IBehaviorIncidentRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MLFeatureCalculationServiceImpl implements IMLFeatureCalculationService {
    
    @Autowired
    private IAttendanceRepository attendanceRepository;
    
    @Autowired
    private IGradeRepository gradeRepository;
    
    @Autowired
    private IBehaviorIncidentRepository behaviorRepository;
    
    @Override
    public StudentFeaturesDTO calculateFeatures(Student student) {
        log.info("Calculating ML features for student: {}", student.getId());
        
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
        LocalDateTime twoWeeksAgoTime = twoWeeksAgo.atStartOfDay();
        
        // Calculate attendance features
        List<Attendance> recentAttendance = getRecentAttendance(student, twoWeeksAgoTime);
        double attendanceRate = calculateAttendanceRate(recentAttendance);
        int daysAbsent = countAbsences(recentAttendance);
        int consecutiveAbsences = calculateConsecutiveAbsences(recentAttendance);
        
        // Calculate grade features using repository for best practice
        List<Grade> currentGrades = gradeRepository.findAllByStudentId(student.getId());
        double averageMarks = calculateAverageMarks(currentGrades);
        int failingCoursesCount = countFailingCourses(currentGrades);
        double lowestGrade = findLowestGrade(currentGrades);
        
        // Calculate behavior features
        List<BehaviorIncident> recentIncidents = getRecentIncidents(student, twoWeeksAgoTime);
        int incidentCount = recentIncidents.size();
        int severityScore = calculateSeverityScore(recentIncidents);
        int daysSinceLastIncident = calculateDaysSinceLastIncident(recentIncidents);
        
        // Calculate demographics
        int weeksEnrolled = calculateWeeksEnrolled(student.getEnrollmentYear());
        int age = calculateAge(student.getDateOfBirth());
        int genderEncoded = student.getGender() == EGender.MALE ? 1 : 0;
        
        // Build and return DTO
        StudentFeaturesDTO features = new StudentFeaturesDTO();
        features.setAttendanceRate(attendanceRate);
        features.setDaysAbsent(daysAbsent);
        features.setConsecutiveAbsences(consecutiveAbsences);
        features.setAverageMarks(averageMarks);
        features.setFailingCoursesCount(failingCoursesCount);
        features.setLowestGrade(lowestGrade);
        features.setIncidentCount(incidentCount);
        features.setSeverityScore(severityScore);
        features.setDaysSinceLastIncident(daysSinceLastIncident);
        features.setWeeksEnrolled(weeksEnrolled);
        features.setAge(age);
        features.setGenderEncoded(genderEncoded);
        
        return features;
    }
    
    @Override
    public List<StudentFeaturesDTO> calculateFeaturesForAll(List<Student> students) {
        log.info("Calculating ML features for {} students", students.size());
        return students.stream()
                .map(this::calculateFeatures)
                .collect(Collectors.toList());
    }
    
    // ===== HELPER METHODS =====
    
    private List<Attendance> getRecentAttendance(Student student, LocalDateTime startDate) {
        // Query attendance for last 2 weeks using repository method
        return attendanceRepository.findByStudent_IdAndCreatedAtAfter(student.getId(), startDate);
    }
    
    // Removed getCurrentSemesterGrades(Student student) in favor of repository method for efficiency and accuracy
    
    private List<BehaviorIncident> getRecentIncidents(Student student, LocalDateTime startDate) {
        // Query behavior incidents for last 2 weeks using repository method
        return behaviorRepository.findByStudent_IdAndCreatedAtAfter(student.getId(), startDate);
    }
    
    private double calculateAttendanceRate(List<Attendance> attendance) {
        if (attendance.isEmpty()) {
            return 0;
        }
        
        long presentCount = attendance.stream()
                .filter(a -> a.getStatus() == EAttendanceStatus.PRESENT)
                .count();
        
        return ((double) presentCount / attendance.size()) * 100.0;
    }
    
    private int countAbsences(List<Attendance> attendance) {
        return (int) attendance.stream()
                .filter(a -> a.getStatus() == EAttendanceStatus.ABSENT)
                .count();
    }
    
    private int calculateConsecutiveAbsences(List<Attendance> attendance) {
        // Sort by date descending (most recent first)
        List<Attendance> sorted = attendance.stream()
                .sorted(Comparator.comparing(Attendance::getDate).reversed())
                .collect(Collectors.toList());
        
        int consecutive = 0;
        for (Attendance att : sorted) {
            if (att.getStatus() == EAttendanceStatus.ABSENT) {
                consecutive++;
            } else {
                break; // Stop at first non-absent day
            }
        }
        
        return consecutive;
    }
    
    private double calculateAverageMarks(List<Grade> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        return grades.stream()
                .mapToDouble(Grade::getMarks)
                .average()
                .orElse(0.0);
    }
    
    private int countFailingCourses(List<Grade> grades) {
        return (int) grades.stream()
                .filter(g -> g.getMarks() < 50.0f)
                .count();
    }
    
    private double findLowestGrade(List<Grade> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }
        
        return grades.stream()
                .mapToDouble(Grade::getMarks)
                .min()
                .orElse(0.0);
    }
    
    private int calculateGradeTrend(Student student, List<Grade> currentGrades) {
        // 0 = DECLINING, 1 = STABLE, 2 = IMPROVING
        if (currentGrades.isEmpty()) {
            return 1; // STABLE (no data)
        }
        
        double currentAvg = calculateAverageMarks(currentGrades);
        
        // Get previous period grades (2-4 weeks ago)
        LocalDate fourWeeksAgo = LocalDate.now().minusWeeks(4);
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
        
        List<Grade> previousGrades = new ArrayList<>();
        if (student.getEnrollments() != null) {
            for (Enrollment enrollment : student.getEnrollments()) {
                if (enrollment.getGrades() != null) {
                    List<Grade> filtered = enrollment.getGrades().stream()
                            .filter(g -> g.getCreatedAt() != null)
                            .filter(g -> {
                                LocalDate gradeDate = g.getCreatedAt().toLocalDate();
                                return !gradeDate.isBefore(fourWeeksAgo) && gradeDate.isBefore(twoWeeksAgo);
                            })
                            .collect(Collectors.toList());
                    previousGrades.addAll(filtered);
                }
            }
        }
        
        if (previousGrades.isEmpty()) {
            return 1; // STABLE (no comparison data)
        }
        
        double previousAvg = calculateAverageMarks(previousGrades);
        double difference = currentAvg - previousAvg;
        
        if (difference > 5.0) {
            return 2; // IMPROVING
        } else if (difference < -5.0) {
            return 0; // DECLINING
        } else {
            return 1; // STABLE
        }
    }
    
    private int calculateSeverityScore(List<BehaviorIncident> incidents) {
        return incidents.stream()
                .mapToInt(incident -> {
                    ESeverityLevel severity = incident.getSeverity();
                    if (severity == null) return 0;
                    
                    switch (severity) {
                        case LOW: return 1;
                        case MEDIUM: return 2;
                        case HIGH: return 3;
                        case CRITICAL: return 5;
                        default: return 0;
                    }
                })
                .sum();
    }
    
    private int calculateDaysSinceLastIncident(List<BehaviorIncident> incidents) {
        if (incidents.isEmpty()) {
            return 999; // Large number indicating no incidents
        }
        
        // Get most recent incident
        LocalDateTime mostRecent = incidents.stream()
                .map(BehaviorIncident::getCreatedAt)
                .filter(date -> date != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        if (mostRecent == null) {
            return 999;
        }
        
        return (int) ChronoUnit.DAYS.between(mostRecent.toLocalDate(), LocalDate.now());
    }
    
    private int calculateWeeksEnrolled(int enrollmentYear) {
        // Assume enrollment starts in September
        LocalDate enrollmentDate = LocalDate.of(enrollmentYear, 9, 1);
        return (int) ChronoUnit.WEEKS.between(enrollmentDate, LocalDate.now());
    }
    
    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 16; // Default age
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
