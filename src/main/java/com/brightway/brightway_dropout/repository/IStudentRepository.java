
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
    List<Student> findBySchoolId(UUID schoolId);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.enrollments WHERE s.school.id = :schoolId")
    List<Student> findAllBySchoolIdWithEnrollments(@Param("schoolId") UUID schoolId);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.school WHERE s.user.id = :userId")
    Optional<Student> findByUserIdWithSchool(@Param("userId") UUID userId);

    List<Student> findByParentId(UUID parentId);


    List<Student> findAllByActive(boolean active);
}
