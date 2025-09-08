package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISchoolRepository extends JpaRepository<School, UUID> {
    Optional<School> findByName(String name);
}
