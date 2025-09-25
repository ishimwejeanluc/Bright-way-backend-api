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
    Optional<Teacher> findById(UUID userId);

    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.id = :id")
    Optional<Teacher> findByIdWithCourses(@Param("id") UUID id);

    @Query("SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.school.id = :schoolId")
    List<Teacher> findAllBySchoolIdWithCourses(@Param("schoolId") UUID schoolId);
}
