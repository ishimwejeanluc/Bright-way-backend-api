package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.BehaviorIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IBehaviorIncidentRepository extends JpaRepository<BehaviorIncident, UUID> {
    
}
