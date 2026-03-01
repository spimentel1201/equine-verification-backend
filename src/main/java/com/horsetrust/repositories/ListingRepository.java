package com.horsetrust.repositories;

import com.horsetrust.models.entities.Listing;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {
    List<Listing> findAllBySeller_Id(UUID sellerId);

    Optional<Listing> findByIdAndSeller_Id(UUID id, UUID sellerId);
}