package com.horsetrust.repositories;

import com.horsetrust.models.entities.HorsePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HorsePhotoRepository extends JpaRepository<HorsePhoto, UUID> {
    long countByHorse_Id(UUID horseId);

    List<HorsePhoto> findAllByHorse_IdOrderByDisplayOrderAsc(UUID horseId);
}
