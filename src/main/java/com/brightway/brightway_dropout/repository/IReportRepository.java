package com.brightway.brightway_dropout.repository;

    


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IReportRepository extends JpaRepository<com.brightway.brightway_dropout.model.School, UUID> {
    
    @Query(value = """
        SELECT 
            COUNT(DISTINCT s.id) as total_students,
            COUNT(DISTINCT c.id) as total_courses,
            COUNT(DISTINCT t.id) as total_teachers,
            COUNT(DISTINCT bi.id) as total_behavior_incidents
        FROM school sc
        LEFT JOIN student s ON sc.id = s.school_id
        LEFT JOIN course c ON sc.id = c.school_id
        LEFT JOIN teacher t ON sc.id = t.school_id
        LEFT JOIN behavior_incident bi ON s.id = bi.student_id
        WHERE sc.id = :schoolId
        """, nativeQuery = true)
    List<Object[]> getSchoolBasicStatistics(@Param("schoolId") UUID schoolId);
    
    // Get school-wide grade statistics
    @Query(value = """
        SELECT 
            ROUND(CAST(AVG(g.marks) AS numeric), 2) as avg_grade,
            ROUND(CAST(MAX(g.marks) AS numeric), 2) as highest_grade,
            ROUND(CAST(MIN(g.marks) AS numeric), 2) as lowest_grade
        FROM grade g
        JOIN enrollment e ON g.enrollment_id = e.id
        JOIN course c ON e.course_id = c.id
        WHERE c.school_id = :schoolId
        """, nativeQuery = true)
    List<Object[]> getSchoolGradeStatistics(@Param("schoolId") UUID schoolId);
    
    // Get school-wide attendance statistics
    @Query(value = """
        SELECT 
            ROUND(CAST(AVG(CASE WHEN a.status = 'PRESENT' THEN 100.0 ELSE 0.0 END) AS numeric), 2) as avg_attendance,
            ROUND(CAST(MAX(CASE WHEN a.status = 'PRESENT' THEN 100.0 ELSE 0.0 END) AS numeric), 2) as highest_attendance,
            ROUND(CAST(MIN(CASE WHEN a.status = 'PRESENT' THEN 100.0 ELSE 0.0 END) AS numeric), 2) as lowest_attendance
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE s.school_id = :schoolId
        """, nativeQuery = true)
    List<Object[]> getSchoolAttendanceStatistics(@Param("schoolId") UUID schoolId);
    
    // Get school-wide risk distribution
    @Query(value = """
        WITH latest_predictions AS (
            SELECT DISTINCT ON (student_id)
                student_id,
                risk_level,
                probability
            FROM dropout_predictions
            ORDER BY student_id, predicted_at DESC
        )
        SELECT 
            COUNT(CASE WHEN lp.risk_level = 'LOW' THEN 1 END) as low_risk_count,
            COUNT(CASE WHEN lp.risk_level = 'MEDIUM' THEN 1 END) as medium_risk_count,
            COUNT(CASE WHEN lp.risk_level = 'HIGH' THEN 1 END) as high_risk_count,
            COUNT(CASE WHEN lp.risk_level = 'CRITICAL' THEN 1 END) as critical_risk_count,
            ROUND(CAST(AVG(lp.probability) AS numeric), 2) as avg_dropout_probability
        FROM student s
        JOIN latest_predictions lp ON s.id = lp.student_id
        WHERE s.school_id = :schoolId
        """, nativeQuery = true)
    List<Object[]> getSchoolRiskDistribution(@Param("schoolId") UUID schoolId);
    
    // Get school-wide top performers
    @Query(value = """
        WITH student_averages AS (
            SELECT 
                s.id as student_id,
                u.name as student_name,
                AVG(g.marks) as avg_grade
            FROM student s
            JOIN users u ON s.user_id = u.id
            JOIN enrollment e ON s.id = e.student_id
            LEFT JOIN grade g ON e.id = g.enrollment_id
            WHERE s.school_id = :schoolId
            GROUP BY s.id, u.name
            HAVING AVG(g.marks) IS NOT NULL
        ),
        ranked_students AS (
            SELECT 
                student_id,
                student_name,
                avg_grade,
                PERCENT_RANK() OVER (ORDER BY avg_grade DESC) as percentile
            FROM student_averages
        )
        SELECT 
            student_id,
            student_name,
            ROUND(CAST(avg_grade AS numeric), 2) as avg_grade,
            ROUND(CAST((1 - percentile) * 100 AS numeric), 2) as percentile_rank
        FROM ranked_students
        WHERE percentile <= 0.1
        ORDER BY avg_grade DESC
        LIMIT 20
        """, nativeQuery = true)
    List<Object[]> getSchoolTopPerformers(@Param("schoolId") UUID schoolId);
    
    // Get school-wide bottom performers
    @Query(value = """
        WITH student_averages AS (
            SELECT 
                s.id as student_id,
                u.name as student_name,
                AVG(g.marks) as avg_grade
            FROM student s
            JOIN users u ON s.user_id = u.id
            JOIN enrollment e ON s.id = e.student_id
            LEFT JOIN grade g ON e.id = g.enrollment_id
            WHERE s.school_id = :schoolId
            GROUP BY s.id, u.name
            HAVING AVG(g.marks) IS NOT NULL
        ),
        ranked_students AS (
            SELECT 
                student_id,
                student_name,
                avg_grade,
                PERCENT_RANK() OVER (ORDER BY avg_grade DESC) as percentile
            FROM student_averages
        )
        SELECT 
            student_id,
            student_name,
            ROUND(CAST(avg_grade AS numeric), 2) as avg_grade,
            ROUND(CAST((1 - percentile) * 100 AS numeric), 2) as percentile_rank
        FROM ranked_students
        WHERE percentile >= 0.9
        ORDER BY avg_grade ASC
        LIMIT 20
        """, nativeQuery = true)
    List<Object[]> getSchoolBottomPerformers(@Param("schoolId") UUID schoolId);
    
    // Get school-wide at-risk students
    @Query(value = """
        WITH latest_predictions AS (
            SELECT DISTINCT ON (student_id)
                student_id,
                risk_level,
                probability
            FROM dropout_predictions
            ORDER BY student_id, created_at DESC
        )
        SELECT 
            s.id as student_id,
            u.name as student_name,
            lp.risk_level,
            ROUND(CAST(lp.probability AS numeric), 2) as dropout_probability,
            ROUND(CAST(AVG(g.marks) AS numeric), 2) as avg_grade,
            ROUND(CAST(COUNT(DISTINCT CASE WHEN a.status = 'PRESENT' THEN a.date END) * 100.0 / 
                NULLIF(COUNT(DISTINCT a.date), 0) AS numeric), 2) as attendance_rate,
            COUNT(DISTINCT bi.id) as behavior_incidents
        FROM student s
        JOIN users u ON s.user_id = u.id
        JOIN latest_predictions lp ON s.id = lp.student_id
        LEFT JOIN enrollment e ON s.id = e.student_id
        LEFT JOIN grade g ON e.id = g.enrollment_id
        LEFT JOIN attendance a ON s.id = a.student_id
        LEFT JOIN behavior_incident bi ON s.id = bi.student_id
        WHERE s.school_id = :schoolId
        AND lp.risk_level IN ('HIGH', 'CRITICAL')
        GROUP BY s.id, u.name, lp.risk_level, lp.probability
        ORDER BY lp.probability DESC
        """, nativeQuery = true)
    List<Object[]> getSchoolAtRiskStudents(@Param("schoolId") UUID schoolId);
    
    // Get course summaries for school
    @Query(value = """
        WITH course_stats AS (
            SELECT 
                c.id as course_id,
                c.name as course_name,
                u.name as teacher_name,
                COUNT(DISTINCT e.student_id) as student_count,
                ROUND(CAST(AVG(g.marks) AS numeric), 2) as avg_grade,
                ROUND(CAST(AVG(CASE WHEN a.status = 'PRESENT' THEN 100.0 ELSE 0.0 END) AS numeric), 2) as attendance_rate,
                COUNT(DISTINCT CASE WHEN lp.risk_level IN ('HIGH', 'CRITICAL') THEN e.student_id END) as at_risk_count
            FROM course c
            LEFT JOIN teacher t ON c.teacher_id = t.id
            LEFT JOIN users u ON t.user_id = u.id
            LEFT JOIN enrollment e ON c.id = e.course_id
            LEFT JOIN grade g ON e.id = g.enrollment_id
            LEFT JOIN attendance a ON e.student_id = a.student_id
            LEFT JOIN LATERAL (
                SELECT dp.risk_level
                FROM dropout_predictions dp
                WHERE dp.student_id = e.student_id
                ORDER BY dp.created_at DESC
                LIMIT 1
            ) lp ON true
            WHERE c.school_id = :schoolId
            GROUP BY c.id, c.name, u.name
        )
        SELECT * FROM course_stats
        ORDER BY course_name
        """, nativeQuery = true)
    List<Object[]> getSchoolCourseSummaries(@Param("schoolId") UUID schoolId);
    
    // ==================== PER-COURSE REPORT QUERIES ====================
    
    // Get all courses for a school with basic info
    @Query(value = """
        SELECT 
            c.id as course_id,
            c.name as course_name,
            u.name as teacher_name,
            COUNT(DISTINCT e.student_id) as total_students
        FROM course c
        LEFT JOIN teacher t ON c.teacher_id = t.id
        LEFT JOIN users u ON t.user_id = u.id
        LEFT JOIN enrollment e ON c.id = e.course_id
        WHERE c.school_id = :schoolId
        GROUP BY c.id, c.name, u.name
        ORDER BY c.name
        """, nativeQuery = true)
    List<Object[]> getSchoolCoursesSummary(@Param("schoolId") UUID schoolId);
    
    // ENHANCED OVERALL REPORT - Comprehensive statistics per course
    @Query(value = """
        WITH student_stats AS (
            SELECT 
                e.course_id,
                s.id as student_id,
                u.name as student_name,
                AVG(g.marks) as avg_grade,
                COUNT(DISTINCT CASE WHEN a.status = 'PRESENT' THEN a.date END) * 100.0 / 
                    NULLIF(COUNT(DISTINCT a.date), 0) as attendance_rate,
                dp.risk_level,
                dp.probability as dropout_probability
            FROM enrollment e
            JOIN student s ON e.student_id = s.id
            JOIN users u ON s.user_id = u.id
            LEFT JOIN grade g ON e.id = g.enrollment_id
            LEFT JOIN attendance a ON s.id = a.student_id
            LEFT JOIN LATERAL (
                SELECT dp2.risk_level, dp2.probability
                FROM dropout_predictions dp2
                WHERE dp2.student_id = s.id
                ORDER BY dp2.created_at DESC
                LIMIT 1
            ) dp ON true
            WHERE e.course_id IN (SELECT id FROM course WHERE school_id = :schoolId)
            GROUP BY e.course_id, s.id, u.name, dp.risk_level, dp.probability
        )
        SELECT 
            c.id as course_id,
            c.name as course_name,
            COUNT(DISTINCT ss.student_id) as total_students,
            ROUND(CAST(AVG(ss.avg_grade) AS numeric), 2) as avg_grade,
            ROUND(CAST(MAX(ss.avg_grade) AS numeric), 2) as highest_grade,
            ROUND(CAST(MIN(ss.avg_grade) AS numeric), 2) as lowest_grade,
            ROUND(CAST(AVG(ss.attendance_rate) AS numeric), 2) as avg_attendance,
            ROUND(CAST(MAX(ss.attendance_rate) AS numeric), 2) as highest_attendance,
            ROUND(CAST(MIN(ss.attendance_rate) AS numeric), 2) as lowest_attendance,
            COUNT(CASE WHEN ss.risk_level = 'LOW' THEN 1 END) as low_risk_count,
            COUNT(CASE WHEN ss.risk_level = 'MEDIUM' THEN 1 END) as medium_risk_count,
            COUNT(CASE WHEN ss.risk_level = 'HIGH' THEN 1 END) as high_risk_count,
            COUNT(CASE WHEN ss.risk_level = 'CRITICAL' THEN 1 END) as critical_risk_count,
            ROUND(CAST(AVG(ss.dropout_probability) AS numeric), 2) as avg_dropout_probability,
            COUNT(DISTINCT bi.id) as total_behavior_incidents
        FROM course c
        LEFT JOIN student_stats ss ON c.id = ss.course_id
        LEFT JOIN enrollment e ON c.id = e.course_id
        LEFT JOIN behavior_incident bi ON e.student_id = bi.student_id
        WHERE c.school_id = :schoolId
        GROUP BY c.id, c.name
        ORDER BY c.name
        """, nativeQuery = true)
    List<Object[]> getSchoolEnhancedOverallReport(@Param("schoolId") UUID schoolId);
    
    // Get top performers per course (top 10%)
    @Query(value = """
        WITH ranked_students AS (
            SELECT 
                e.course_id,
                s.id as student_id,
                u.name as student_name,
                AVG(g.marks) as avg_grade,
                PERCENT_RANK() OVER (PARTITION BY e.course_id ORDER BY AVG(g.marks) DESC) as percentile
            FROM enrollment e
            JOIN student s ON e.student_id = s.id
            JOIN users u ON s.user_id = u.id
            LEFT JOIN grade g ON e.id = g.enrollment_id
            WHERE e.course_id IN (SELECT id FROM course WHERE school_id = :schoolId)
            GROUP BY e.course_id, s.id, u.name
            HAVING AVG(g.marks) IS NOT NULL
        )
        SELECT 
            course_id,
            student_id,
            student_name,
            ROUND(CAST(avg_grade AS numeric), 2) as avg_grade,
            ROUND(CAST((1 - percentile) * 100 AS numeric), 2) as percentile_rank
        FROM ranked_students
        WHERE percentile <= 0.1
        ORDER BY course_id, avg_grade DESC
        """, nativeQuery = true)
    List<Object[]> getTopPerformersBySchool(@Param("schoolId") UUID schoolId);
    
    // Get bottom performers per course (bottom 10%)
    @Query(value = """
        WITH ranked_students AS (
            SELECT 
                e.course_id,
                s.id as student_id,
                u.name as student_name,
                AVG(g.marks) as avg_grade,
                PERCENT_RANK() OVER (PARTITION BY e.course_id ORDER BY AVG(g.marks) DESC) as percentile
            FROM enrollment e
            JOIN student s ON e.student_id = s.id
            JOIN users u ON s.user_id = u.id
            LEFT JOIN grade g ON e.id = g.enrollment_id
            WHERE e.course_id IN (SELECT id FROM course WHERE school_id = :schoolId)
            GROUP BY e.course_id, s.id, u.name
            HAVING AVG(g.marks) IS NOT NULL
        )
        SELECT 
            course_id,
            student_id,
            student_name,
            ROUND(CAST(avg_grade AS numeric), 2) as avg_grade,
            ROUND(CAST((1 - percentile) * 100 AS numeric), 2) as percentile_rank
        FROM ranked_students
        WHERE percentile >= 0.9
        ORDER BY course_id, avg_grade ASC
        """, nativeQuery = true)
    List<Object[]> getBottomPerformersBySchool(@Param("schoolId") UUID schoolId);
    
    // Get at-risk students details per course
    @Query(value = """
        SELECT 
            e.course_id,
            s.id as student_id,
            u.name as student_name,
            dp.risk_level,
            ROUND(CAST(dp.probability AS numeric), 2) as dropout_probability,
            ROUND(CAST(AVG(g.marks) AS numeric), 2) as avg_grade,
            ROUND(CAST(COUNT(DISTINCT CASE WHEN a.status = 'PRESENT' THEN a.date END) * 100.0 / 
                NULLIF(COUNT(DISTINCT a.date), 0) AS numeric), 2) as attendance_rate,
            COUNT(DISTINCT bi.id) as behavior_incidents
        FROM enrollment e
        JOIN student s ON e.student_id = s.id
        JOIN users u ON s.user_id = u.id
        LEFT JOIN grade g ON e.id = g.enrollment_id
        LEFT JOIN attendance a ON s.id = a.student_id
        LEFT JOIN behavior_incident bi ON s.id = bi.student_id
        LEFT JOIN LATERAL (
            SELECT dp2.risk_level, dp2.probability
            FROM dropout_predictions dp2
            WHERE dp2.student_id = s.id
            ORDER BY dp2.created_at DESC
            LIMIT 1
        ) dp ON true
        WHERE e.course_id IN (SELECT id FROM course WHERE school_id = :schoolId)
        AND dp.risk_level IN ('HIGH', 'CRITICAL')
        GROUP BY e.course_id, s.id, u.name, dp.risk_level, dp.probability
        ORDER BY e.course_id, dp.probability DESC
        """, nativeQuery = true)
    List<Object[]> getAtRiskStudentsBySchool(@Param("schoolId") UUID schoolId);
    
    // GRADES REPORT - Per student per course
    @Query(value = """
        SELECT 
            c.id as course_id,
            c.name as course_name,
            s.id as student_id,
            u.name as student_name,
            COALESCE(SUM(CASE WHEN g.grade_type = 'ASSIGNMENT' THEN g.marks ELSE 0 END), 0) as assignment_total,
            COALESCE(COUNT(CASE WHEN g.grade_type = 'ASSIGNMENT' THEN 1 END), 0) as assignment_count,
            COALESCE(MAX(CASE WHEN g.grade_type = 'EXAM' THEN g.marks END), 0) as final_exam,
            COALESCE(SUM(CASE WHEN g.grade_type = 'QUIZ' THEN g.marks ELSE 0 END), 0) as quiz_total,
            COALESCE(COUNT(CASE WHEN g.grade_type = 'QUIZ' THEN 1 END), 0) as quiz_count,
            COALESCE(SUM(CASE WHEN g.grade_type = 'GROUPWORK' THEN g.marks ELSE 0 END), 0) as groupwork_total,
            COALESCE(COUNT(CASE WHEN g.grade_type = 'GROUPWORK' THEN 1 END), 0) as groupwork_count,
            ROUND(CAST(AVG(g.marks) AS numeric), 2) as overall_average
        FROM course c
        JOIN enrollment e ON c.id = e.course_id
        JOIN student s ON e.student_id = s.id
        JOIN users u ON s.user_id = u.id
        LEFT JOIN grade g ON e.id = g.enrollment_id
        WHERE c.school_id = :schoolId
        GROUP BY c.id, c.name, s.id, u.name
        ORDER BY c.name, u.name
        """, nativeQuery = true)
    List<Object[]> getSchoolGradesReport(@Param("schoolId") UUID schoolId);
    
    // ATTENDANCE REPORT - Per student per course
    @Query(value = """
        SELECT 
            c.id as course_id,
            c.name as course_name,
            s.id as student_id,
            u.name as student_name,
            a.date as attendance_date,
            COALESCE(CAST(a.status AS text), 'N/A') as attendance_status
        FROM course c
        JOIN enrollment e ON c.id = e.course_id
        JOIN student s ON e.student_id = s.id
        JOIN users u ON s.user_id = u.id
        LEFT JOIN attendance a ON s.id = a.student_id
        WHERE c.school_id = :schoolId
        ORDER BY c.name, u.name, a.date
        """, nativeQuery = true)
    List<Object[]> getSchoolAttendanceReport(@Param("schoolId") UUID schoolId);
    
    // Get all unique attendance dates for students in courses of a school
    @Query(value = """
        SELECT DISTINCT a.date
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE s.school_id = :schoolId
        ORDER BY a.date
        """, nativeQuery = true)
    List<java.sql.Date> getAttendanceDatesBySchoolId(@Param("schoolId") UUID schoolId);
}
