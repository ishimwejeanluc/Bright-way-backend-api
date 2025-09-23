package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByName(String name);
    List<Course> findBySchoolId(UUID schoolId);
}
