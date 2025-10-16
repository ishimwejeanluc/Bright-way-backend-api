package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.foreign.Linker.Option;
import java.util.Optional;
import java.util.UUID;

public interface IEnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    Optional<Enrollment> findById(UUID enrollmentId);
}
