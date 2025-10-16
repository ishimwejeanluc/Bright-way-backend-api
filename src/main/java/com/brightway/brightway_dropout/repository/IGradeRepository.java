package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IGradeRepository extends JpaRepository<Grade, UUID> {
}
