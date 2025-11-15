   

package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IGradeRepository extends JpaRepository<Grade, UUID> {
    
    // Subject performance - subject name and current average filtered by school
    @Query(value = """
        SELECT 
            c.name,
            ROUND(AVG(g.marks)::numeric, 1)
        FROM course c
        LEFT JOIN enrollment e ON c.id = e.course_id
        LEFT JOIN grade g ON e.id = g.enrollment_id
        LEFT JOIN student s ON e.student_id = s.id
        WHERE s.school_id = :schoolId
        GROUP BY c.id, c.name
        ORDER BY c.name
        """, nativeQuery = true)
    List<Object[]> findSubjectPerformance(@Param("schoolId") UUID schoolId);
    
    // Overall stats filtered by school
    @Query(value = """
        SELECT 
            ROUND((AVG(g.marks) / 4)::numeric, 2),
            ROUND(MAX(g.marks)::numeric, 2),
            ROUND(MIN(g.marks)::numeric, 2)
        FROM grade g
        JOIN enrollment e ON g.enrollment_id = e.id
        JOIN student s ON e.student_id = s.id
        WHERE s.school_id = :schoolId
        """, nativeQuery = true)
    Object[] findOverallStats(@Param("schoolId") UUID schoolId);

    // Get student grades by teacher's userId, grouped by course
        // Get student grades by teacherId, grouped by course
        @Query(value = """
            SELECT 
                c.name as course_name,
                s.id as student_id,
                u.name as student_name,
                g.name as mark_name,
                g.marks,
                g.grade_type
            FROM grade g
            JOIN enrollment e ON g.enrollment_id = e.id
            JOIN course c ON e.course_id = c.id
            JOIN student s ON e.student_id = s.id
            JOIN users u ON s.user_id = u.id
            JOIN teacher t ON c.teacher_id = t.id
            WHERE t.id = :teacherId
            ORDER BY c.name, u.name, g.name
            """, nativeQuery = true)
        List<Object[]> findStudentGradesByTeacherId(@Param("teacherId") UUID teacherId);

         // Average GPA for a specific student
    @Query(value = """
        SELECT ROUND(AVG(g.marks)::numeric, 2)
        FROM grade g
        JOIN enrollment e ON g.enrollment_id = e.id
        WHERE e.student_id = :studentId
        """, nativeQuery = true)
    Double findAverageGPAForStudent(@Param("studentId") UUID studentId);

    // Performance trend by grade type for a specific student
    @Query(value = """
        SELECT g.grade_type, ROUND(AVG(g.marks)::numeric, 2)
        FROM grade g
        JOIN enrollment e ON g.enrollment_id = e.id
        WHERE e.student_id = :studentId
        GROUP BY g.grade_type
        ORDER BY g.grade_type
        """, nativeQuery = true)
    List<Object[]> findPerformanceTrendByGradeTypeForStudent(@Param("studentId") UUID studentId);
}
