
package com.brightway.brightway_dropout.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brightway.brightway_dropout.model.Student;

@Repository
public interface IStudentRepository extends JpaRepository<Student, UUID> {

    long countBySchoolId(UUID schoolId);
    List<Student> findBySchoolId(UUID schoolId);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.school.id = :schoolId")
    List<Student> findAllBySchoolIdWithEnrollments(@Param("schoolId") UUID schoolId);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.school WHERE s.user.id = :userId")
    Optional<Student> findByUserIdWithSchool(@Param("userId") UUID userId);

    List<Student> findByParentId(UUID parentId);


    List<Student> findAllByActive(boolean active);
    
    // Get student details for government dashboard
    @Query(value = """
        SELECT 
            s.id as student_id,
            u.name as student_name,
            s.date_of_birth,
            COUNT(DISTINCT e.course_id) as courses_enrolled
        FROM student s
        JOIN users u ON s.user_id = u.id
        LEFT JOIN enrollment e ON s.id = e.student_id
        WHERE s.school_id = :schoolId
        GROUP BY s.id, u.name, s.date_of_birth
        ORDER BY u.name
        """, nativeQuery = true)
    List<Object[]> findStudentDetailsForGovernment(@Param("schoolId") UUID schoolId);
    
    // Get comprehensive student profile for principal
    @Query(value = """
        SELECT 
            s.id,
            u.name,
            CONCAT('ST-', LPAD(CAST(s.enrollment_year AS TEXT), 4, '0'), '-', SUBSTRING(CAST(s.id AS TEXT), 1, 4)),
            sch.name,
            pu.name,
            pu.phone,
            pu.email,
            p.occupation,
            dp.risk_level,
            dp.probability,
            -- Overall attendance percentage
            ROUND(CAST((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(a.id), 0) AS NUMERIC), 0),
            -- Average grade score
            ROUND(CAST(AVG(g.marks) AS NUMERIC), 2)
        FROM student s
        JOIN users u ON s.user_id = u.id
        JOIN school sch ON s.school_id = sch.id
        LEFT JOIN parent p ON s.parent_id = p.id
        LEFT JOIN users pu ON p.user_id = pu.id
        LEFT JOIN dropout_predictions dp ON s.id = dp.student_id 
            AND dp.created_at = (
                SELECT MAX(created_at) 
                FROM dropout_predictions 
                WHERE student_id = s.id
            )
        LEFT JOIN attendance a ON s.id = a.student_id
        LEFT JOIN enrollment e ON s.id = e.student_id
        LEFT JOIN grade g ON e.id = g.enrollment_id
        WHERE s.id = :studentId
        GROUP BY s.id, u.name, s.enrollment_year, sch.name, 
                 pu.name, pu.phone, pu.email, p.occupation,
                 dp.risk_level, dp.probability
        """, nativeQuery = true)
    List<Object[]> findStudentProfileById(@Param("studentId") UUID studentId);
    
    // Get attendance overview by periods for a student
    @Query(value = """
        SELECT 
            -- This month
            ROUND(CAST((COUNT(CASE WHEN a.status = 'PRESENT' AND a.date >= DATE_TRUNC('month', CURRENT_DATE) THEN 1 END) * 100.0) 
                / NULLIF(COUNT(CASE WHEN a.date >= DATE_TRUNC('month', CURRENT_DATE) THEN 1 END), 0) AS NUMERIC), 0),
            -- This semester (last 6 months)
            ROUND(CAST((COUNT(CASE WHEN a.status = 'PRESENT' AND a.date >= CURRENT_DATE - INTERVAL '6 months' THEN 1 END) * 100.0) 
                / NULLIF(COUNT(CASE WHEN a.date >= CURRENT_DATE - INTERVAL '6 months' THEN 1 END), 0) AS NUMERIC), 0),
            -- This year
            ROUND(CAST((COUNT(CASE WHEN a.status = 'PRESENT' AND a.date >= DATE_TRUNC('year', CURRENT_DATE) THEN 1 END) * 100.0) 
                / NULLIF(COUNT(CASE WHEN a.date >= DATE_TRUNC('year', CURRENT_DATE) THEN 1 END), 0) AS NUMERIC), 0)
        FROM attendance a
        WHERE a.student_id = :studentId
        """, nativeQuery = true)
    List<Object[]> findAttendanceOverviewByPeriods(@Param("studentId") UUID studentId);
}
