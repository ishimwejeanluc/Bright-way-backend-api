package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IParentRepository extends JpaRepository<Parent, UUID> {
  Optional<Parent> findByUserId(UUID userId);
}
