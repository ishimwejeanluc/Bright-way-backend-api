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
}
