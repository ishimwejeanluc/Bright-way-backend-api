package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAuthRepository extends JpaRepository<User , UUID> {
    Optional<User> findByEmail(String email);

}
