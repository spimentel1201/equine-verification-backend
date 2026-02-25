package com.horsetrust.repositories;

import com.horsetrust.models.entities.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceRepository extends JpaRepository<Evidence, UUID> {
    List<Evidence> findAllByListing_Id(UUID listingId);

    List<Evidence> findAllByHorse_Id(UUID horseId);

    List<Evidence> findAllByUploader_Id(UUID uploaderId);

    Optional<Evidence> findByIdAndUploader_Id(UUID id, UUID uploaderId);
}
