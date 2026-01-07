
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
}
