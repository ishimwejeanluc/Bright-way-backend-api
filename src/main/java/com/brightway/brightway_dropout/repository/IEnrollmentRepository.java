package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    Optional<Enrollment> findById(UUID enrollmentId);
    List<Enrollment> findByCourseId(UUID courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    List<Enrollment> findByStudentId(UUID studentId);
    
    // Get course names for a student
    @Query(value = """
        SELECT c.name
        FROM enrollment e
        JOIN course c ON e.course_id = c.id
        WHERE e.student_id = :studentId
        ORDER BY c.name
        """, nativeQuery = true)
    List<String> findCourseNamesByStudentId(@Param("studentId") UUID studentId);
}
