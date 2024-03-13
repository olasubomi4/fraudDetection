package com.example.fraudDetection.repository;

import com.example.fraudDetection.entity.FlaggedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFlaggedUserRepository extends JpaRepository<FlaggedUser, Long> {
    FlaggedUser findByUserId(String userId);
}
