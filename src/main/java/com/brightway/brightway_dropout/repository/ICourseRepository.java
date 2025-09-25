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

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.enrollments LEFT JOIN FETCH c.teacher WHERE c.school.id = :schoolId")
    List<Course> findAllBySchoolIdWithDetails(@Param("schoolId") UUID schoolId);
}
