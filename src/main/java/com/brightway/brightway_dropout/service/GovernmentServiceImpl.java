package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.government.response.*;
import com.brightway.brightway_dropout.model.DropoutPrediction;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GovernmentServiceImpl implements IGovernmentService {
    
    private final IStudentRepository studentRepository;
    private final ITeacherRepository teacherRepository;
    private final IAttendanceRepository attendanceRepository;
    private final IDropoutPredictionRepository dropoutPredictionRepository;
    private final IGradeRepository gradeRepository;
    private final ISchoolRepository schoolRepository;
    private final IBehaviorIncidentRepository behaviorIncidentRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final ICourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public GovDashboardOverviewResponseDTO getDashboardOverview(LocalDate startDate, LocalDate endDate, UUID schoolId) {
        // Set default date range (last 6 months) if not provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusMonths(6);
        }

        // Get school IDs to filter by
        List<UUID> schoolIds = null;
        if (schoolId != null) {
            schoolIds = Collections.singletonList(schoolId);
        }

        // Calculate summary using native queries
        SummaryDTO summary = calculateSummary(schoolIds, startDate, endDate);

        // Calculate dropout risk trends by month using native queries
        List<RiskTrendDTO> dropoutRiskTrends = calculateDropoutRiskTrends(schoolIds, startDate, endDate);

        // Calculate policy impact by school using native queries
        List<PolicyImpactDTO> policyImpactBySchool = calculatePolicyImpactBySchool(startDate, endDate, schoolId);

        return new GovDashboardOverviewResponseDTO(summary, dropoutRiskTrends, policyImpactBySchool);
    }

    private SummaryDTO calculateSummary(List<UUID> schoolIds, LocalDate startDate, LocalDate endDate) {
        // Count students and teachers using repository count methods
        int totalStudents = 0;
        int totalTeachers = 0;
        
        if (schoolIds == null) {
            // Count all students and teachers
            totalStudents = (int) studentRepository.count();
            totalTeachers = (int) teacherRepository.count();
        } else {
            // Count for specific schools
            for (UUID schoolId : schoolIds) {
                totalStudents += (int) studentRepository.countBySchoolId(schoolId);
                totalTeachers += (int) teacherRepository.countBySchoolId(schoolId);
            }
        }

        // Calculate average attendance using native query
        Double averageAttendance = schoolIds == null ?
            attendanceRepository.calculateAverageAttendanceForAllSchools(startDate, endDate) :
            attendanceRepository.calculateAverageAttendanceForSpecificSchools(startDate, endDate, schoolIds);
        
        if (averageAttendance == null) {
            averageAttendance = 0.0;
        }

        // Calculate dropout rate using native query
        Double dropoutRate = schoolIds == null ?
            dropoutPredictionRepository.calculateDropoutRateForAllSchools() :
            dropoutPredictionRepository.calculateDropoutRateForSpecificSchools(schoolIds);
        
        if (dropoutRate == null) {
            dropoutRate = 0.0;
        }

        return new SummaryDTO(totalStudents, totalTeachers, averageAttendance, dropoutRate);
    }

    private List<RiskTrendDTO> calculateDropoutRiskTrends(List<UUID> schoolIds, LocalDate startDate, LocalDate endDate) {
        // Use native query to get risk trends grouped by month
        List<Object[]> results = schoolIds == null ?
            dropoutPredictionRepository.calculateRiskTrendsByMonthForAllSchools(startDate, endDate) :
            dropoutPredictionRepository.calculateRiskTrendsByMonthForSpecificSchools(startDate, endDate, schoolIds);
        
        return results.stream()
            .map(row -> new RiskTrendDTO(
                (String) row[0],           // month
                ((Number) row[1]).longValue(),  // lowRisk
                ((Number) row[2]).longValue(),  // mediumRisk
                ((Number) row[3]).longValue(),  // highRisk
                ((Number) row[4]).longValue()   // criticalRisk
            ))
            .collect(Collectors.toList());
    }

    private List<PolicyImpactDTO> calculatePolicyImpactBySchool(LocalDate startDate, LocalDate endDate, UUID filterSchoolId) {
        // Get schools to analyze
        List<School> schools = filterSchoolId != null ?
            Collections.singletonList(schoolRepository.findById(filterSchoolId).orElse(null)) :
            schoolRepository.findAll();
        
        schools = schools.stream().filter(Objects::nonNull).collect(Collectors.toList());
        
        return schools.stream()
            .map(school -> {
                // Calculate attendance using native query
                Double attendance = attendanceRepository.calculateAttendanceForSchool(startDate, endDate, school.getId());
                if (attendance == null) {
                    attendance = 0.0;
                }

                // Calculate performance using native query
                Double performance = gradeRepository.calculateAveragePerformanceForSchool(school.getId());
                if (performance == null) {
                    performance = 0.0;
                }

                return new PolicyImpactDTO(
                    school.getId().toString(),
                    school.getName(),
                    attendance,
                    performance
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GovSchoolsOverviewResponseDTO getSchoolsOverview() {
        // Total number of schools
        int totalSchools = (int) schoolRepository.count();
        
        // Total number of students
        int totalStudents = (int) studentRepository.count();
        
        // Overall dropout rate using existing native query
        Double overallDropoutRate = dropoutPredictionRepository.calculateDropoutRateForAllSchools();
        if (overallDropoutRate == null) {
            overallDropoutRate = 0.0;
        }
        
        // Total at-risk students using native query
        Integer totalAtRiskStudents = dropoutPredictionRepository.countTotalAtRiskStudents();
        if (totalAtRiskStudents == null) {
            totalAtRiskStudents = 0;
        }
        
        // Get school list with dropout rates using native query
        List<Object[]> schoolData = schoolRepository.findSchoolsOverviewWithDropoutRate();
        List<SchoolOverviewItemDTO> schools = schoolData.stream()
            .map(row -> new SchoolOverviewItemDTO(
                (UUID) row[0],    // school_id
                (String) row[1],  // school_name
                (String) row[2],  // region
                ((Number) row[3]).doubleValue()  // dropout_rate
            ))
            .collect(Collectors.toList());
        
        return new GovSchoolsOverviewResponseDTO(
            totalSchools,
            totalStudents,
            overallDropoutRate,
            totalAtRiskStudents,
            schools
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GovSchoolProfileOverviewDTO getSchoolProfileOverview(UUID schoolId) {
        // Get school basic info and aggregated data using native query
        List<Object[]> result = schoolRepository.findSchoolProfileOverview(schoolId);
        
        if (result == null || result.isEmpty()) {
            throw new RuntimeException("School not found with ID: " + schoolId);
        }
        
        Object[] schoolData = result.get(0);
        
        String schoolName = (String) schoolData[0];
        String location = (String) schoolData[1];
        String principalName = (String) schoolData[2];
        String description = (String) schoolData[3];
        int totalEnrollment = ((Number) schoolData[4]).intValue();
        int teachingStaff = ((Number) schoolData[5]).intValue();
        double dropoutRate = ((Number) schoolData[6]).doubleValue();
        
        // Calculate average attendance using existing native query
        Double avgAttendance = attendanceRepository.calculateAverageAttendanceForSchoolAllTime(schoolId);
        if (avgAttendance == null) {
            avgAttendance = 0.0;
        }
        
        // Calculate average grade using existing native query
        Double avgGrade = gradeRepository.calculateAveragePerformanceForSchool(schoolId);
        if (avgGrade == null) {
            avgGrade = 0.0;
        }
        
        // Count behavior incidents using native query
        Integer behaviorIncidents = behaviorIncidentRepository.countBySchoolId(schoolId);
        if (behaviorIncidents == null) {
            behaviorIncidents = 0;
        }
        
        // Count at-risk students using native query
        Integer atRiskStudents = dropoutPredictionRepository.countAtRiskStudentsBySchoolId(schoolId);
        if (atRiskStudents == null) {
            atRiskStudents = 0;
        }
        
        return new GovSchoolProfileOverviewDTO(
            schoolName,
            location,
            principalName,
            description,
            totalEnrollment,
            teachingStaff,
            dropoutRate,
            avgAttendance,
            avgGrade,
            behaviorIncidents,
            atRiskStudents
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GovStudentOverviewResponseDTO getStudentOverview() {
        // Get student overview grouped by schools using native query
        List<Object[]> schoolData = schoolRepository.findStudentOverviewBySchools();
        
        List<GovStudentOverviewItemDTO> schools = schoolData.stream()
            .map(row -> new GovStudentOverviewItemDTO(
                (UUID) row[0],              // school_id
                (String) row[1],            // school_name
                (String) row[2],            // region
                ((Number) row[3]).intValue() // number_of_students
            ))
            .collect(Collectors.toList());
        
        return new GovStudentOverviewResponseDTO(schools);
    }

    @Override
    @Transactional(readOnly = true)
    public GovStudentDetailsResponseDTO getStudentDetails(UUID schoolId) {
        // Get student basic data using native query
        List<Object[]> studentData = studentRepository.findStudentDetailsForGovernment(schoolId);
        
        if (studentData.isEmpty()) {
            return new GovStudentDetailsResponseDTO(0.0, 0.0, 0, new ArrayList<>());
        }
        
        List<GovStudentDetailItemDTO> students = new ArrayList<>();
        double totalMarks = 0.0;
        double totalAttendance = 0.0;
        int totalCoursesCount = 0;
        int studentCount = 0;
        
        for (Object[] row : studentData) {
            UUID studentId = (UUID) row[0];
            String studentName = (String) row[1];
            java.sql.Date dateOfBirth = (java.sql.Date) row[2];
            int coursesEnrolled = ((Number) row[3]).intValue();
            
            // Calculate age
            int age = LocalDate.now().getYear() - dateOfBirth.toLocalDate().getYear();
            
            // Get course names using repository
            List<String> courseNames = enrollmentRepository.findCourseNamesByStudentId(studentId);
            
            // Get average marks using existing repository method
            Double avgMarks = gradeRepository.findAverageGPAForStudent(studentId);
            if (avgMarks == null) {
                avgMarks = 0.0;
            }
            
            // Get average attendance using existing repository method
            Double avgAttendance = attendanceRepository.findAttendanceRateForStudent(studentId);
            if (avgAttendance == null) {
                avgAttendance = 0.0;
            }
            
            // Get dropout prediction probability and risk level using existing JPA method
            Optional<DropoutPrediction> predictionOpt = dropoutPredictionRepository.findTopByStudentIdOrderByPredictedAtDesc(studentId);
            double probability = 0.0;
            String riskLevel = "UNKNOWN";
            if (predictionOpt.isPresent()) {
                DropoutPrediction prediction = predictionOpt.get();
                probability = prediction.getProbability();
                riskLevel = prediction.getRiskLevel() != null ? prediction.getRiskLevel().toString() : "UNKNOWN";
            }
            
            // Get behavior incidents count
            Integer behaviorIncidents = behaviorIncidentRepository.countByStudentId(studentId);
            if (behaviorIncidents == null) {
                behaviorIncidents = 0;
            }
            
            students.add(new GovStudentDetailItemDTO(
                studentId,
                studentName,
                coursesEnrolled,
                courseNames,
                avgMarks,
                avgAttendance,
                probability,
                riskLevel,
                age,
                behaviorIncidents
            ));
            
            totalMarks += avgMarks;
            totalAttendance += avgAttendance;
            totalCoursesCount += coursesEnrolled;
            studentCount++;
        }
        
        // Calculate averages
        double avgMarks = studentCount > 0 ? Math.round((totalMarks / studentCount) * 10.0) / 10.0 : 0.0;
        double avgAttendance = studentCount > 0 ? Math.round((totalAttendance / studentCount) * 10.0) / 10.0 : 0.0;
        
        return new GovStudentDetailsResponseDTO(avgMarks, avgAttendance, totalCoursesCount, students);
    }
    
    @Override
    public GovTeacherOverviewResponseDTO getTeacherOverview() {
        List<Object[]> results = schoolRepository.findTeacherOverviewBySchools();
        
        List<GovTeacherOverviewItemDTO> schools = results.stream()
            .map(row -> new GovTeacherOverviewItemDTO(
                (UUID) row[0],           // school id
                (String) row[1],         // school name
                (String) row[2],         // region
                ((Long) row[3]).intValue() // number of teachers
            ))
            .collect(Collectors.toList());
        
        return new GovTeacherOverviewResponseDTO(schools);
    }
    
    @Override
    public GovTeacherDetailsResponseDTO getTeacherDetails(UUID schoolId) {
        List<Object[]> results = teacherRepository.findTeacherDetailsForGovernment(schoolId);
        
        List<GovTeacherDetailItemDTO> teachers = new ArrayList<>();
        int totalCourses = 0;
        int totalStudents = 0;
        
        for (Object[] row : results) {
            UUID teacherId = (UUID) row[0];
            String name = (String) row[1];
            String specialization = (String) row[2];
            int coursesTeaching = ((Long) row[3]).intValue();
            
            // Get course names
            List<String> courseNames = courseRepository.findCourseNamesByTeacherId(teacherId);
            
            // Get total students across all courses
            Integer studentCount = courseRepository.countStudentsByTeacherId(teacherId);
            int numberOfStudents = studentCount != null ? studentCount : 0;
            
            teachers.add(new GovTeacherDetailItemDTO(
                teacherId,
                name,
                specialization,
                coursesTeaching,
                courseNames,
                numberOfStudents
            ));
            
            totalCourses += coursesTeaching;
            totalStudents += numberOfStudents;
        }
        
        return new GovTeacherDetailsResponseDTO(totalCourses, totalStudents, teachers);
    }
}
