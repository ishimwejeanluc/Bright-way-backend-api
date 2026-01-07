package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITeacherRepository extends JpaRepository<Teacher, UUID> {
        long countBySchoolId(UUID schoolId);
    Optional<Teacher> findById(UUID userId);
    Optional <Teacher> findByUserId(UUID userId);


    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.id = :id")
    Optional<Teacher> findByIdWithCourses(@Param("id") UUID id);

    @Query("SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.school.id = :schoolId")
    List<Teacher> findAllBySchoolIdWithCourses(@Param("schoolId") UUID schoolId);

    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.user.id = :userId")
    Optional<Teacher> findByUserIdWithCourses(@Param("userId") UUID userId);
    
    // Get teacher details for government dashboard
    @Query(value = """
        SELECT 
            t.id as teacher_id,
            u.name as teacher_name,
            t.specialization,
            COUNT(DISTINCT c.id) as courses_teaching
        FROM teacher t
        JOIN users u ON t.user_id = u.id
        LEFT JOIN course c ON t.id = c.teacher_id
        WHERE t.school_id = :schoolId
        GROUP BY t.id, u.name, t.specialization
        ORDER BY u.name
        """, nativeQuery = true)
    List<Object[]> findTeacherDetailsForGovernment(@Param("schoolId") UUID schoolId);
}
