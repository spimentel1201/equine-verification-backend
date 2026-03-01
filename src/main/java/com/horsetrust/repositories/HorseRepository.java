package com.horsetrust.repositories;

import com.horsetrust.models.entities.Horse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorseRepository extends JpaRepository<Horse, UUID>, JpaSpecificationExecutor<Horse> {

    @EntityGraph(attributePaths = { "owner" })
    List<Horse> findAllByOwner_Id(UUID ownerId);

    @EntityGraph(attributePaths = { "owner" })
    Optional<Horse> findByIdAndOwner_Id(UUID id, UUID ownerId);
}