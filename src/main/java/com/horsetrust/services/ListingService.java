package com.horsetrust.services;

import com.horsetrust.api.dto.*;
import com.horsetrust.common.exception.*;
import com.horsetrust.models.entities.*;
import com.horsetrust.models.enums.ListingStatus;
import com.horsetrust.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;
    private final UserRepository userRepository; // asumido
    // private final VerificationRepository verificationRepository; // no requerido para listing ticket

    @Transactional
    public ListingResponse createListing(CreateListingRequest req, UUID sellerId) {
        Horse horse = horseRepository.findById(req.horseId())
                .orElseThrow(() -> new NotFoundException("Horse not found"));

        // Regla razonable: solo podés listar tus caballos
        if (!horse.getOwner().getId().equals(sellerId)) {
            throw new ForbiddenOperationException("You can only create listings for your own horses");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String title = generateTitle(horse);

        Listing listing = Listing.builder()
                .title(title)
                .description(req.description())
                .conditions(req.conditions())
                .price(req.price())
                .location(req.location())
                .status(ListingStatus.DRAFT)
                .seller(seller)
                .horse(horse)
                .build();

        Listing saved = listingRepository.save(listing);
        return toResponse(saved);
    }

    @Transactional
    public ListingResponse updateListing(UUID listingId, UpdateListingRequest req, UUID sellerId) {
        Listing listing = listingRepository.findByIdAndSeller_Id(listingId, sellerId)
                .orElseThrow(() -> new NotFoundException("Listing not found"));

        // Regla del ticket
        if (!(listing.getStatus() == ListingStatus.DRAFT || listing.getStatus() == ListingStatus.REJECTED)) {
            throw new ForbiddenOperationException("Listing can only be updated when status is DRAFT or REJECTED");
        }

        if (req.price() != null) listing.setPrice(req.price());
        if (req.location() != null) listing.setLocation(req.location());
        if (req.description() != null) listing.setDescription(req.description());
        if (req.conditions() != null) listing.setConditions(req.conditions());

        return toResponse(listing);
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> myListings(UUID sellerId) {
        return listingRepository.findAllBySeller_Id(sellerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateTitle(Horse horse) {
        String base = horse.getName() + " - " + horse.getBreed();
        base = base.trim().replaceAll("\\s+", " ");
        if (base.length() < 5) base = "Horse Listing";
        return base.length() > 200 ? base.substring(0, 200) : base;
    }

    private ListingResponse toResponse(Listing l) {
        return new ListingResponse(
                l.getId(),
                l.getHorse().getId(),
                l.getSeller().getId(),
                l.getTitle(),
                l.getDescription(),
                l.getConditions(),
                l.getPrice(),
                l.getLocation(),
                l.getStatus().name(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}