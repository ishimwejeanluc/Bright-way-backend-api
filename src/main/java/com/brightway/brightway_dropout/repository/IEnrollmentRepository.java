package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    Optional<Enrollment> findById(UUID enrollmentId);
    List<Enrollment> findByCourseId(UUID courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
}
