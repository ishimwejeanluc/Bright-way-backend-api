package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
