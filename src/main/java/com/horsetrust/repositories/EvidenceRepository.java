package com.horsetrust.repositories;

import com.horsetrust.models.entities.Evidence;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceRepository extends JpaRepository<Evidence, UUID>, JpaSpecificationExecutor<Evidence> {

    @EntityGraph(attributePaths = { "listing", "horse", "uploader" })
    List<Evidence> findAllByListing_Id(UUID listingId);

    @EntityGraph(attributePaths = { "listing", "horse", "uploader" })
    List<Evidence> findAllByHorse_Id(UUID horseId);

    @EntityGraph(attributePaths = { "listing", "horse", "uploader" })
    List<Evidence> findAllByUploader_Id(UUID uploaderId);

    @EntityGraph(attributePaths = { "listing", "horse", "uploader" })
    Optional<Evidence> findByIdAndUploader_Id(UUID id, UUID uploaderId);
}
