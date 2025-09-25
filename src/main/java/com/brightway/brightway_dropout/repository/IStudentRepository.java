package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IStudentRepository extends JpaRepository<Student, UUID> {
    List<Student> findBySchoolId(UUID schoolId);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.school.id = :schoolId")
    List<Student> findAllBySchoolIdWithEnrollments(@Param("schoolId") UUID schoolId);
}
