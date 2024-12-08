package com.example.profile_service.repository;


import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import com.example.profile_service.entity.UserProfile;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends Neo4jRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userID);
    Optional<UserProfile> findByEmail(String email);
}