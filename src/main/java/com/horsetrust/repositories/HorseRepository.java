package com.horsetrust.repositories;

import com.horsetrust.models.entities.Horse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorseRepository extends JpaRepository<Horse, UUID> {
    List<Horse> findAllByOwner_Id(UUID ownerId);
    Optional<Horse> findByIdAndOwner_Id(UUID id, UUID ownerId);
}