package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISchoolRepository extends JpaRepository<School, UUID> {
    Optional<School> findByName(String name);
    Optional<School> findById(UUID schoolId);
    Optional<School> findByPrincipalId(UUID principalId);
    
    // Get school overview with id, name, region, and dropout rate
    @Query(value = """
        SELECT 
            s.id,
            s.name as school_name,
            s.region,
            COALESCE(ROUND((COUNT(CASE WHEN st.status = 'DROPPED' THEN 1 END) * 100.0) / NULLIF(COUNT(st.id), 0), 1), 0.0) as dropout_rate
        FROM school s
        LEFT JOIN student st ON s.id = st.school_id
        GROUP BY s.id, s.name, s.region
        ORDER BY s.name
        """, nativeQuery = true)
    List<Object[]> findSchoolsOverviewWithDropoutRate();
    
    // Get single school profile overview with aggregated data
    @Query(value = """
        SELECT 
            s.name as school_name,
            s.region as location,
            u.name as principal_name,
            s.address as description,
            COUNT(DISTINCT st.id) as total_enrollment,
            COUNT(DISTINCT t.id) as teaching_staff,
            COALESCE(ROUND((COUNT(CASE WHEN st.status = 'DROPPED' THEN 1 END) * 100.0) / NULLIF(COUNT(st.id), 0), 1), 0.0) as dropout_rate
        FROM school s
        LEFT JOIN users u ON s.principal = u.id
        LEFT JOIN student st ON s.id = st.school_id
        LEFT JOIN teacher t ON s.id = t.school_id
        WHERE s.id = :schoolId
        GROUP BY s.id, s.name, s.region, u.name, s.address
        """, nativeQuery = true)
    List<Object[]> findSchoolProfileOverview(@Param("schoolId") UUID schoolId);
    
    // Get student overview grouped by schools with student counts
    @Query(value = """
        SELECT 
            s.id,
            s.name as school_name,
            s.region,
            COUNT(st.id) as number_of_students
        FROM school s
        LEFT JOIN student st ON s.id = st.school_id
        GROUP BY s.id, s.name, s.region
        ORDER BY s.name
        """, nativeQuery = true)
    List<Object[]> findStudentOverviewBySchools();
    
    // Get teacher overview grouped by schools with teacher counts
    @Query(value = """
        SELECT 
            s.id,
            s.name as school_name,
            s.region,
            COUNT(t.id) as number_of_teachers
        FROM school s
        LEFT JOIN teacher t ON s.id = t.school_id
        GROUP BY s.id, s.name, s.region
        ORDER BY s.name
        """, nativeQuery = true)
    List<Object[]> findTeacherOverviewBySchools();
}
