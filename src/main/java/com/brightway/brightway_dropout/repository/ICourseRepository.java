package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByName(String name);
    List<Course> findBySchoolId(UUID schoolId);

    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.enrollments e " +
           "LEFT JOIN FETCH e.student " +
           "LEFT JOIN FETCH c.teacher " +
           "WHERE c.school.id = :schoolId")
    List<Course> findAllBySchoolIdWithDetails(@Param("schoolId") UUID schoolId);
    
    @Query(value = """
        SELECT COUNT(DISTINCT e.student_id)
        FROM enrollment e
        JOIN student s ON e.student_id = s.id
        JOIN dropout_predictions dp ON s.id = dp.student_id
        WHERE e.course_id = :courseId
        AND dp.risk_level IN ('HIGH', 'CRITICAL')
        AND dp.created_at = (
            SELECT MAX(dp2.created_at)
            FROM dropout_predictions dp2
            WHERE dp2.student_id = s.id
        )
        """, nativeQuery = true)
    int countAtRiskStudentsByCourseId(@Param("courseId") UUID courseId);
}
