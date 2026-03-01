package com.horsetrust.repositories;

import com.horsetrust.models.entities.Verification;
import com.horsetrust.models.enums.VerificationStatus;
import com.horsetrust.models.enums.VerificationTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationRepository
        extends JpaRepository<Verification, UUID>, JpaSpecificationExecutor<Verification> {
    List<Verification> findAllByTargetAndTargetId(VerificationTarget target, UUID targetId);

    List<Verification> findAllByStatus(VerificationStatus status);

    Optional<Verification> findByIdAndVerifier_Id(UUID id, UUID verifierId);

    boolean existsByTargetAndTargetId(VerificationTarget target, UUID targetId);
}