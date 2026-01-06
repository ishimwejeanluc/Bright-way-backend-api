package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.government.response.*;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
}
