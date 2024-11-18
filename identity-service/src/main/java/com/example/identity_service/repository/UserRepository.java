package com.example.identity_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identity_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String id);
}
