package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.report.response.*;
import com.brightway.brightway_dropout.enumeration.EReportType;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.IReportRepository;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {
    
    private final ISchoolRepository schoolRepository;
    private final IReportRepository reportRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Object getGovernmentReport(EReportType reportType) {
        if (reportType == EReportType.OVERALL) {
            return getGovernmentOverallReport();
        } else {
            return getGovernmentDetailedReport(reportType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GovernmentOverallReportDTO getGovernmentOverallReport() {
        List<School> schools = schoolRepository.findAll();
        List<SchoolOverallReportDTO> schoolReports = new ArrayList<>();
        int totalStudents = 0;
        double totalAttendance = 0.0;
        double totalGrade = 0.0;
        int totalAtRisk = 0;
        int schoolCount = schools.size();
        for (School school : schools) {
            SchoolOverallReportDTO report = getSchoolOverallReport(school.getId());
            schoolReports.add(report);
            totalStudents += report.getTotalStudents();
            totalAttendance += report.getAverageAttendance();
            totalGrade += report.getAverageGrade();
            if (report.getRiskDistribution() != null) {
                totalAtRisk += report.getRiskDistribution().getHighRiskCount();
                totalAtRisk += report.getRiskDistribution().getCriticalRiskCount();
            }
        }
        double avgAttendance = schoolCount > 0 ? totalAttendance / schoolCount : 0.0;
        double avgGrade = schoolCount > 0 ? totalGrade / schoolCount : 0.0;
        return new GovernmentOverallReportDTO(
                schoolCount,
                totalStudents,
                avgAttendance,
                avgGrade,
                totalAtRisk,
                schoolReports
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GovernmentReportResponseDTO getGovernmentDetailedReport(EReportType reportType) {
        List<School> schools = schoolRepository.findAll();
        List<SchoolOverallReportDTO> schoolReports = new ArrayList<>();
        int totalStudents = 0;
        double totalAttendance = 0.0;
        double totalGrade = 0.0;
        int totalAtRisk = 0;
        int schoolCount = schools.size();
        for (School school : schools) {
            SchoolOverallReportDTO report = getSchoolOverallReport(school.getId());
            schoolReports.add(report);
            totalStudents += report.getTotalStudents();
            totalAttendance += report.getAverageAttendance();
            totalGrade += report.getAverageGrade();
            if (report.getRiskDistribution() != null) {
                totalAtRisk += report.getRiskDistribution().getHighRiskCount();
                totalAtRisk += report.getRiskDistribution().getCriticalRiskCount();
            }
        }
        double avgAttendance = schoolCount > 0 ? totalAttendance / schoolCount : 0.0;
        double avgGrade = schoolCount > 0 ? totalGrade / schoolCount : 0.0;
        return new GovernmentReportResponseDTO(
                reportType.name(),
                schoolCount,
                totalStudents,
                avgAttendance,
                avgGrade,
                totalAtRisk,
                schoolReports
        );
    }
    @Override
    @Transactional(readOnly = true)
    public Object getPrincipalReport(UUID schoolId, EReportType reportType) {
        if (reportType == EReportType.OVERALL) {
            return getSchoolOverallReport(schoolId);
        } else {
            return getSchoolDetailedReport(schoolId, reportType);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public SchoolOverallReportDTO getSchoolOverallReport(UUID schoolId) {
        // Verify school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School with ID " + schoolId + " not found"));
        
        SchoolOverallReportDTO report = new SchoolOverallReportDTO();
        report.setSchoolId(schoolId);
        report.setSchoolName(school.getName());
        
        // Get basic statistics
        List<Object[]> basicStatsList = reportRepository.getSchoolBasicStatistics(schoolId);
        if (!basicStatsList.isEmpty()) {
            Object[] basicStats = basicStatsList.get(0);
            report.setTotalStudents(basicStats[0] != null ? ((Number) basicStats[0]).intValue() : 0);
            report.setTotalCourses(basicStats[1] != null ? ((Number) basicStats[1]).intValue() : 0);
            report.setTotalTeachers(basicStats[2] != null ? ((Number) basicStats[2]).intValue() : 0);
            report.setTotalBehaviorIncidents(basicStats[3] != null ? ((Number) basicStats[3]).intValue() : 0);
        } else {
            report.setTotalStudents(0);
            report.setTotalCourses(0);
            report.setTotalTeachers(0);
            report.setTotalBehaviorIncidents(0);
        }
        
        // Get grade statistics
        List<Object[]> gradeStatsList = reportRepository.getSchoolGradeStatistics(schoolId);
        if (!gradeStatsList.isEmpty()) {
            Object[] gradeStats = gradeStatsList.get(0);
            report.setAverageGrade(gradeStats[0] != null ? ((Number) gradeStats[0]).doubleValue() : 0.0);
            report.setHighestGrade(gradeStats[1] != null ? ((Number) gradeStats[1]).doubleValue() : 0.0);
            report.setLowestGrade(gradeStats[2] != null ? ((Number) gradeStats[2]).doubleValue() : 0.0);
        } else {
            report.setAverageGrade(0.0);
            report.setHighestGrade(0.0);
            report.setLowestGrade(0.0);
        }
        
        // Get attendance statistics
        List<Object[]> attendanceStatsList = reportRepository.getSchoolAttendanceStatistics(schoolId);
        if (!attendanceStatsList.isEmpty()) {
            Object[] attendanceStats = attendanceStatsList.get(0);
            report.setAverageAttendance(attendanceStats[0] != null ? ((Number) attendanceStats[0]).doubleValue() : 0.0);
            report.setHighestAttendance(attendanceStats[1] != null ? ((Number) attendanceStats[1]).doubleValue() : 0.0);
            report.setLowestAttendance(attendanceStats[2] != null ? ((Number) attendanceStats[2]).doubleValue() : 0.0);
        } else {
            report.setAverageAttendance(0.0);
            report.setHighestAttendance(0.0);
            report.setLowestAttendance(0.0);
        }
        
        // Get risk distribution
        List<Object[]> riskStatsList = reportRepository.getSchoolRiskDistribution(schoolId);
        int lowRisk = 0;
        int mediumRisk = 0;
        int highRisk = 0;
        int criticalRisk = 0;
        Double avgDropoutProb = 0.0;
        
        if (!riskStatsList.isEmpty()) {
            Object[] riskStats = riskStatsList.get(0);
            lowRisk = riskStats[0] != null ? ((Number) riskStats[0]).intValue() : 0;
            mediumRisk = riskStats[1] != null ? ((Number) riskStats[1]).intValue() : 0;
            highRisk = riskStats[2] != null ? ((Number) riskStats[2]).intValue() : 0;
            criticalRisk = riskStats[3] != null ? ((Number) riskStats[3]).intValue() : 0;
            avgDropoutProb = riskStats[4] != null ? ((Number) riskStats[4]).doubleValue() : 0.0;
        }
        int totalWithRisk = lowRisk + mediumRisk + highRisk + criticalRisk;
        
        RiskDistributionDTO riskDist = new RiskDistributionDTO();
        riskDist.setLowRiskCount(lowRisk);
        riskDist.setMediumRiskCount(mediumRisk);
        riskDist.setHighRiskCount(highRisk);
        riskDist.setCriticalRiskCount(criticalRisk);
        
        if (totalWithRisk > 0) {
            riskDist.setLowRiskPercentage(Math.round((lowRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
            riskDist.setMediumRiskPercentage(Math.round((mediumRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
            riskDist.setHighRiskPercentage(Math.round((highRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
            riskDist.setCriticalRiskPercentage(Math.round((criticalRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
            
            // Determine trend based on risk distribution
            double atRiskPercentage = ((highRisk + criticalRisk) * 100.0) / totalWithRisk;
            if (atRiskPercentage < 20) {
                riskDist.setTrend("STABLE");
            } else if (atRiskPercentage < 40) {
                riskDist.setTrend("CONCERNING");
            } else {
                riskDist.setTrend("CRITICAL");
            }
        } else {
            riskDist.setLowRiskPercentage(0.0);
            riskDist.setMediumRiskPercentage(0.0);
            riskDist.setHighRiskPercentage(0.0);
            riskDist.setCriticalRiskPercentage(0.0);
            riskDist.setTrend("STABLE");
        }
        
        report.setRiskDistribution(riskDist);
        report.setAverageDropoutProbability(avgDropoutProb);
        
        // Get top performers
        List<Object[]> topPerformersData = reportRepository.getSchoolTopPerformers(schoolId);
        List<PerformerDTO> topPerformers = topPerformersData.stream()
            .map(row -> new PerformerDTO(
                (UUID) row[0],
                (String) row[1],
                row[2] != null ? ((Number) row[2]).doubleValue() : 0.0,
                row[3] != null ? ((Number) row[3]).doubleValue() : 0.0
            ))
            .collect(Collectors.toList());
        report.setTopPerformers(topPerformers);
        
        // Get bottom performers
        List<Object[]> bottomPerformersData = reportRepository.getSchoolBottomPerformers(schoolId);
        List<PerformerDTO> bottomPerformers = bottomPerformersData.stream()
            .map(row -> new PerformerDTO(
                (UUID) row[0],
                (String) row[1],
                row[2] != null ? ((Number) row[2]).doubleValue() : 0.0,
                row[3] != null ? ((Number) row[3]).doubleValue() : 0.0
            ))
            .collect(Collectors.toList());
        report.setBottomPerformers(bottomPerformers);
        
        // Get at-risk students
        List<Object[]> atRiskData = reportRepository.getSchoolAtRiskStudents(schoolId);
        List<AtRiskStudentDTO> atRiskStudents = atRiskData.stream()
            .map(row -> {
                AtRiskStudentDTO dto = new AtRiskStudentDTO();
                dto.setStudentId((UUID) row[0]);
                dto.setStudentName((String) row[1]);
                dto.setRiskLevel((String) row[2]);
                dto.setDropoutProbability(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0);
                dto.setAverageGrade(row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
                dto.setAttendanceRate(row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
                dto.setBehaviorIncidents(row[6] != null ? ((Number) row[6]).intValue() : 0);
                
                // Determine primary concern
                double attendance = dto.getAttendanceRate();
                double grade = dto.getAverageGrade();
                int incidents = dto.getBehaviorIncidents();
                
                int concerns = 0;
                String primaryConcern = "Unknown";
                
                if (attendance < 75) { concerns++; primaryConcern = "Low Attendance"; }
                if (grade < 60) { concerns++; primaryConcern = concerns == 1 ? "Poor Grades" : primaryConcern; }
                if (incidents > 3) { concerns++; primaryConcern = concerns == 1 ? "Behavior Issues" : primaryConcern; }
                
                dto.setPrimaryConcern(concerns > 1 ? "Multiple Factors" : primaryConcern);
                
                return dto;
            })
            .collect(Collectors.toList());
        report.setAtRiskStudents(atRiskStudents);
        
        // Get course summaries
        List<Object[]> courseSummariesData = reportRepository.getSchoolCourseSummaries(schoolId);
        List<CourseSummaryDTO> courseSummaries = courseSummariesData.stream()
            .map(row -> new CourseSummaryDTO(
                (UUID) row[0],
                (String) row[1],
                (String) row[2],
                row[3] != null ? ((Number) row[3]).intValue() : 0,
                row[4] != null ? ((Number) row[4]).doubleValue() : 0.0,
                row[5] != null ? ((Number) row[5]).doubleValue() : 0.0,
                row[6] != null ? ((Number) row[6]).intValue() : 0
            ))
            .collect(Collectors.toList());
        report.setCourseSummaries(courseSummaries);
        
        return report;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SchoolReportResponseDTO getSchoolDetailedReport(UUID schoolId, EReportType reportType) {
        // Verify school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School with ID " + schoolId + " not found"));
        
        // Get all courses for the school
        List<Object[]> courseSummary = reportRepository.getSchoolCoursesSummary(schoolId);
        
        List<CourseReportSection> courseReports = new ArrayList<>();
        int totalStudents = 0;
        
        switch (reportType) {
            case ATTENDANCE:
                courseReports = generateSchoolAttendanceReport(schoolId, courseSummary);
                break;
                
            case GRADES:
                courseReports = generateSchoolGradesReport(schoolId, courseSummary);
                break;
                
            case OVERALL:
            default:
                courseReports = generateSchoolOverallReport(schoolId, courseSummary);
                break;
        }
        
        // Calculate total students across all courses (sum, not unique)
        for (CourseReportSection section : courseReports) {
            totalStudents += section.getStudentCount();
        }
        
        return new SchoolReportResponseDTO(
            schoolId,
            school.getName(),
            reportType.name(),
            courseReports.size(),
            totalStudents,
            courseReports
        );
    }
    
    private List<CourseReportSection> generateSchoolOverallReport(UUID schoolId, List<Object[]> courseSummary) {
        List<Object[]> overallData = reportRepository.getSchoolEnhancedOverallReport(schoolId);
        List<Object[]> topPerformersData = reportRepository.getTopPerformersBySchool(schoolId);
        List<Object[]> bottomPerformersData = reportRepository.getBottomPerformersBySchool(schoolId);
        List<Object[]> atRiskData = reportRepository.getAtRiskStudentsBySchool(schoolId);
        
        // Group data by course
        Map<UUID, EnhancedCourseOverallSummaryDTO> summaryMap = new LinkedHashMap<>();
        Map<UUID, List<PerformerDTO>> topPerformersMap = new LinkedHashMap<>();
        Map<UUID, List<PerformerDTO>> bottomPerformersMap = new LinkedHashMap<>();
        Map<UUID, List<AtRiskStudentDTO>> atRiskMap = new LinkedHashMap<>();
        
        // Process overall statistics
        for (Object[] row : overallData) {
            UUID courseId = (UUID) row[0];
            
            EnhancedCourseOverallSummaryDTO summary = new EnhancedCourseOverallSummaryDTO();
            summary.setTotalStudents(row[2] != null ? ((Number) row[2]).intValue() : 0);
            summary.setAverageGrade(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0);
            summary.setHighestGrade(row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
            summary.setLowestGrade(row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
            summary.setAverageAttendance(row[6] != null ? ((Number) row[6]).doubleValue() : 0.0);
            summary.setHighestAttendance(row[7] != null ? ((Number) row[7]).doubleValue() : 0.0);
            summary.setLowestAttendance(row[8] != null ? ((Number) row[8]).doubleValue() : 0.0);
            
            // Risk distribution
            int lowRisk = row[9] != null ? ((Number) row[9]).intValue() : 0;
            int mediumRisk = row[10] != null ? ((Number) row[10]).intValue() : 0;
            int highRisk = row[11] != null ? ((Number) row[11]).intValue() : 0;
            int criticalRisk = row[12] != null ? ((Number) row[12]).intValue() : 0;
            int totalWithRisk = lowRisk + mediumRisk + highRisk + criticalRisk;
            
            RiskDistributionDTO riskDist = new RiskDistributionDTO();
            riskDist.setLowRiskCount(lowRisk);
            riskDist.setMediumRiskCount(mediumRisk);
            riskDist.setHighRiskCount(highRisk);
            riskDist.setCriticalRiskCount(criticalRisk);
            
            if (totalWithRisk > 0) {
                riskDist.setLowRiskPercentage(Math.round((lowRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
                riskDist.setMediumRiskPercentage(Math.round((mediumRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
                riskDist.setHighRiskPercentage(Math.round((highRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
                riskDist.setCriticalRiskPercentage(Math.round((criticalRisk * 100.0 / totalWithRisk) * 100.0) / 100.0);
                
                // Determine trend based on risk distribution
                double atRiskPercentage = ((highRisk + criticalRisk) * 100.0) / totalWithRisk;
                if (atRiskPercentage < 20) {
                    riskDist.setTrend("STABLE");
                } else if (atRiskPercentage < 40) {
                    riskDist.setTrend("CONCERNING");
                } else {
                    riskDist.setTrend("CRITICAL");
                }
            } else {
                riskDist.setLowRiskPercentage(0.0);
                riskDist.setMediumRiskPercentage(0.0);
                riskDist.setHighRiskPercentage(0.0);
                riskDist.setCriticalRiskPercentage(0.0);
                riskDist.setTrend("STABLE");
            }
            
            summary.setRiskDistribution(riskDist);
            summary.setAverageDropoutProbability(row[13] != null ? ((Number) row[13]).doubleValue() : 0.0);
            summary.setTotalBehaviorIncidents(row[14] != null ? ((Number) row[14]).intValue() : 0);
            
            summaryMap.put(courseId, summary);
        }
        
        // Process top performers
        for (Object[] row : topPerformersData) {
            UUID courseId = (UUID) row[0];
            PerformerDTO performer = new PerformerDTO(
                (UUID) row[1],
                (String) row[2],
                row[3] != null ? ((Number) row[3]).doubleValue() : 0.0,
                row[4] != null ? ((Number) row[4]).doubleValue() : 0.0
            );
            topPerformersMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(performer);
        }
        
        // Process bottom performers
        for (Object[] row : bottomPerformersData) {
            UUID courseId = (UUID) row[0];
            PerformerDTO performer = new PerformerDTO(
                (UUID) row[1],
                (String) row[2],
                row[3] != null ? ((Number) row[3]).doubleValue() : 0.0,
                row[4] != null ? ((Number) row[4]).doubleValue() : 0.0
            );
            bottomPerformersMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(performer);
        }
        
        // Process at-risk students
        for (Object[] row : atRiskData) {
            UUID courseId = (UUID) row[0];
            
            AtRiskStudentDTO atRiskStudent = new AtRiskStudentDTO();
            atRiskStudent.setStudentId((UUID) row[1]);
            atRiskStudent.setStudentName((String) row[2]);
            atRiskStudent.setRiskLevel((String) row[3]);
            atRiskStudent.setDropoutProbability(row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
            atRiskStudent.setAverageGrade(row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
            atRiskStudent.setAttendanceRate(row[6] != null ? ((Number) row[6]).doubleValue() : 0.0);
            atRiskStudent.setBehaviorIncidents(row[7] != null ? ((Number) row[7]).intValue() : 0);
            
            // Determine primary concern
            double attendance = atRiskStudent.getAttendanceRate();
            double grade = atRiskStudent.getAverageGrade();
            int incidents = atRiskStudent.getBehaviorIncidents();
            
            int concerns = 0;
            String primaryConcern = "Unknown";
            
            if (attendance < 75) { concerns++; primaryConcern = "Low Attendance"; }
            if (grade < 60) { concerns++; primaryConcern = concerns == 1 ? "Poor Grades" : primaryConcern; }
            if (incidents > 3) { concerns++; primaryConcern = concerns == 1 ? "Behavior Issues" : primaryConcern; }
            
            atRiskStudent.setPrimaryConcern(concerns > 1 ? "Multiple Factors" : primaryConcern);
            
            atRiskMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(atRiskStudent);
        }
        
        // Build course sections
        List<CourseReportSection> sections = new ArrayList<>();
        for (Object[] courseInfo : courseSummary) {
            UUID courseId = (UUID) courseInfo[0];
            String courseName = (String) courseInfo[1];
            String teacherName = (String) courseInfo[2];
            int studentCount = courseInfo[3] != null ? ((Number) courseInfo[3]).intValue() : 0;
            
            CourseReportSection section = new CourseReportSection();
            section.setCourseId(courseId);
            section.setCourseName(courseName);
            section.setTeacherName(teacherName);
            section.setStudentCount(studentCount);
            
            EnhancedCourseOverallSummaryDTO summary = summaryMap.get(courseId);
            if (summary != null) {
                summary.setTopPerformers(topPerformersMap.getOrDefault(courseId, new ArrayList<>()));
                summary.setBottomPerformers(bottomPerformersMap.getOrDefault(courseId, new ArrayList<>()));
                summary.setAtRiskStudents(atRiskMap.getOrDefault(courseId, new ArrayList<>()));
            } else {
                summary = new EnhancedCourseOverallSummaryDTO();
                summary.setTotalStudents(studentCount);
                summary.setAverageGrade(0.0);
                summary.setHighestGrade(0.0);
                summary.setLowestGrade(0.0);
                summary.setAverageAttendance(0.0);
                summary.setHighestAttendance(0.0);
                summary.setLowestAttendance(0.0);
                summary.setRiskDistribution(new RiskDistributionDTO());
                summary.setAverageDropoutProbability(0.0);
                summary.setTotalBehaviorIncidents(0);
                summary.setTopPerformers(new ArrayList<>());
                summary.setBottomPerformers(new ArrayList<>());
                summary.setAtRiskStudents(new ArrayList<>());
            }
            
            section.setReportData(summary);
            sections.add(section);
        }
        
        return sections;
    }
    
    private List<CourseReportSection> generateSchoolGradesReport(UUID schoolId, List<Object[]> courseSummary) {
        List<Object[]> gradesData = reportRepository.getSchoolGradesReport(schoolId);
        
        // Group by course
        Map<UUID, List<GradesReportDTO>> courseGradesMap = new LinkedHashMap<>();
        
        for (Object[] row : gradesData) {
            UUID courseId = (UUID) row[0];
            UUID studentId = (UUID) row[2];
            String studentName = (String) row[3];
            
            GradesReportDTO gradeDTO = new GradesReportDTO();
            gradeDTO.setStudentId(studentId);
            gradeDTO.setStudentName(studentName);
            
            Double assignmentTotal = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
            Integer assignmentCount = row[5] != null ? ((Number) row[5]).intValue() : 0;
            gradeDTO.setAssignmentTotal(assignmentTotal);
            gradeDTO.setAssignmentCount(assignmentCount);
            gradeDTO.setAssignmentAverage(assignmentCount > 0 ? 
                Math.round((assignmentTotal / assignmentCount) * 100.0) / 100.0 : 0.0);
            
            gradeDTO.setFinalExam(row[6] != null ? ((Number) row[6]).doubleValue() : 0.0);
            
            Double quizTotal = row[7] != null ? ((Number) row[7]).doubleValue() : 0.0;
            Integer quizCount = row[8] != null ? ((Number) row[8]).intValue() : 0;
            gradeDTO.setQuizTotal(quizTotal);
            gradeDTO.setQuizCount(quizCount);
            gradeDTO.setQuizAverage(quizCount > 0 ? 
                Math.round((quizTotal / quizCount) * 100.0) / 100.0 : 0.0);
            
            Double groupworkTotal = row[9] != null ? ((Number) row[9]).doubleValue() : 0.0;
            Integer groupworkCount = row[10] != null ? ((Number) row[10]).intValue() : 0;
            gradeDTO.setGroupworkTotal(groupworkTotal);
            gradeDTO.setGroupworkCount(groupworkCount);
            gradeDTO.setGroupworkAverage(groupworkCount > 0 ? 
                Math.round((groupworkTotal / groupworkCount) * 100.0) / 100.0 : 0.0);
            
            gradeDTO.setOverallAverage(row[11] != null ? ((Number) row[11]).doubleValue() : 0.0);
            
            courseGradesMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(gradeDTO);
        }
        
        // Build course sections
        List<CourseReportSection> sections = new ArrayList<>();
        for (Object[] courseInfo : courseSummary) {
            UUID courseId = (UUID) courseInfo[0];
            String courseName = (String) courseInfo[1];
            String teacherName = (String) courseInfo[2];
            int studentCount = courseInfo[3] != null ? ((Number) courseInfo[3]).intValue() : 0;
            
            CourseReportSection section = new CourseReportSection();
            section.setCourseId(courseId);
            section.setCourseName(courseName);
            section.setTeacherName(teacherName);
            section.setStudentCount(studentCount);
            
            CourseGradesDataDTO gradesDataDTO = new CourseGradesDataDTO();
            gradesDataDTO.setStudentGrades(courseGradesMap.getOrDefault(courseId, new ArrayList<>()));
            section.setReportData(gradesDataDTO);
            
            sections.add(section);
        }
        
        return sections;
    }
    
    private List<CourseReportSection> generateSchoolAttendanceReport(UUID schoolId, List<Object[]> courseSummary) {
        List<Object[]> attendanceData = reportRepository.getSchoolAttendanceReport(schoolId);
        List<Date> dateData = reportRepository.getAttendanceDatesBySchoolId(schoolId);
        
        // Convert dates to strings for consistency
        List<String> allDates = dateData.stream()
            .map(date -> date.toLocalDate().toString())
            .collect(Collectors.toList());
        
        // Group attendance by course and student
        Map<UUID, Map<UUID, AttendanceReportDTO>> courseAttendanceMap = new LinkedHashMap<>();
        
        for (Object[] row : attendanceData) {
            UUID courseId = (UUID) row[0];
            UUID studentId = (UUID) row[2];
            String studentName = (String) row[3];
            Date attendanceDate = (Date) row[4];
            String status = (String) row[5];
            
            Map<UUID, AttendanceReportDTO> studentMap = courseAttendanceMap.computeIfAbsent(courseId, k -> new LinkedHashMap<>());
            
            AttendanceReportDTO dto = studentMap.computeIfAbsent(studentId, id -> {
                AttendanceReportDTO newDto = new AttendanceReportDTO();
                newDto.setStudentId(id);
                newDto.setStudentName(studentName);
                newDto.setAttendanceByDate(new LinkedHashMap<>());
                return newDto;
            });
            
            if (attendanceDate != null && !"N/A".equals(status)) {
                String dateStr = attendanceDate.toLocalDate().toString();
                dto.getAttendanceByDate().put(dateStr, status);
            }
        }
        
        // Calculate attendance rates and build sections
        List<CourseReportSection> sections = new ArrayList<>();
        for (Object[] courseInfo : courseSummary) {
            UUID courseId = (UUID) courseInfo[0];
            String courseName = (String) courseInfo[1];
            String teacherName = (String) courseInfo[2];
            int studentCount = courseInfo[3] != null ? ((Number) courseInfo[3]).intValue() : 0;
            
            CourseReportSection section = new CourseReportSection();
            section.setCourseId(courseId);
            section.setCourseName(courseName);
            section.setTeacherName(teacherName);
            section.setStudentCount(studentCount);
            
            List<AttendanceReportDTO> studentAttendance = new ArrayList<>(
                courseAttendanceMap.getOrDefault(courseId, new LinkedHashMap<>()).values()
            );
            
            // Calculate attendance rate for each student
            for (AttendanceReportDTO dto : studentAttendance) {
                long presentCount = dto.getAttendanceByDate().values().stream()
                    .filter(status -> "PRESENT".equals(status))
                    .count();
                int totalDays = dto.getAttendanceByDate().size();
                dto.setAttendanceRate(totalDays > 0 ? 
                    Math.round((presentCount * 100.0 / totalDays) * 100.0) / 100.0 : 0.0);
            }
            
            CourseAttendanceDataDTO attendanceDataDTO = new CourseAttendanceDataDTO();
            attendanceDataDTO.setDates(allDates);
            attendanceDataDTO.setStudentAttendance(studentAttendance);
            section.setReportData(attendanceDataDTO);
            
            sections.add(section);
        }
        
        return sections;
    }
}
