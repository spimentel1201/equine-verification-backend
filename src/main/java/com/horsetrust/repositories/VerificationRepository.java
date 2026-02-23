package com.horsetrust.repositories;

import com.horsetrust.models.entities.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {
    boolean existsByListing_Horse_Id(UUID horseId);
}