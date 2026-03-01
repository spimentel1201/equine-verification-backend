package com.horsetrust.services;

import com.horsetrust.api.dto.*;
import com.horsetrust.common.exception.*;
import com.horsetrust.models.entities.*;
import com.horsetrust.models.enums.*;
import com.horsetrust.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    @Transactional
    public VerificationResponse requestVerification(UUID listingId, UUID sellerId) {
        Listing listing = listingRepository.findByIdAndSeller_Id(listingId, sellerId)
                .orElseThrow(() -> new ForbiddenOperationException("Listing not found or does not belong to you"));

        if (listing.getStatus() != ListingStatus.DRAFT && listing.getStatus() != ListingStatus.REJECTED) {
            throw new ForbiddenOperationException(
                    "Verification can only be requested when listing status is DRAFT or REJECTED");
        }

        // Cambiar listing a PENDING_VERIFICATION y bloquear edición
        listing.setStatus(ListingStatus.PENDING_VERIFICATION);

        Verification verification = Verification.builder()
                .target(VerificationTarget.LISTING)
                .targetId(listingId)
                .status(VerificationStatus.PENDING)
                .build();

        Verification saved = verificationRepository.save(verification);
        return toResponse(saved);
    }

    @Transactional
    public VerificationResponse review(UUID verificationId, ReviewVerificationRequest req, UUID adminId) {
        Verification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new NotFoundException("Verification not found"));

        if (verification.getStatus() != VerificationStatus.PENDING
                && verification.getStatus() != VerificationStatus.IN_PROGRESS) {
            throw new ForbiddenOperationException("This verification has already been resolved");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin user not found"));

        verification.setVerifier(admin);
        verification.setStatus(req.status());
        verification.setNotes(req.notes());

        if (verification.getTarget() == VerificationTarget.LISTING) {
            Listing listing = listingRepository.findById(verification.getTargetId())
                    .orElseThrow(() -> new NotFoundException("Associated listing not found"));

            switch (req.status()) {
                case APPROVED -> {
                    listing.setStatus(ListingStatus.VERIFIED);
                    verification.setValidUntil(LocalDateTime.now().plusYears(1));
                }
                case REJECTED -> listing.setStatus(ListingStatus.REJECTED);
                case NEEDS_INFO -> listing.setStatus(ListingStatus.PENDING_VERIFICATION);
                case IN_PROGRESS -> listing.setStatus(ListingStatus.PENDING_VERIFICATION);
                default -> throw new ForbiddenOperationException("Invalid review status: " + req.status());
            }
        }

        return toResponse(verification);
    }

    @Transactional(readOnly = true)
    public List<VerificationResponse> getByListing(UUID listingId) {
        return verificationRepository
                .findAllByTargetAndTargetId(VerificationTarget.LISTING, listingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<VerificationResponse> search(VerificationTarget target, VerificationStatus status,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Specification<Verification> spec = Specification
                .where(com.horsetrust.repositories.specifications.VerificationSpecification.hasTarget(target))
                .and(com.horsetrust.repositories.specifications.VerificationSpecification.hasStatus(status))
                .and(com.horsetrust.repositories.specifications.VerificationSpecification.createdAtBetween(startDate,
                        endDate));

        Page<Verification> page = verificationRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    private VerificationResponse toResponse(Verification v) {
        return new VerificationResponse(
                v.getId(),
                v.getTarget().name(),
                v.getTargetId(),
                v.getStatus().name(),
                v.getNotes(),
                v.getVerifier() != null ? v.getVerifier().getId() : null,
                v.getValidUntil(),
                v.getCreatedAt(),
                v.getUpdatedAt());
    }
}
