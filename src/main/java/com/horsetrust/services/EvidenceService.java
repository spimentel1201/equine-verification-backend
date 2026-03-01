package com.horsetrust.services;

import com.horsetrust.api.dto.*;
import com.horsetrust.common.exception.*;
import com.horsetrust.models.entities.*;
import com.horsetrust.models.enums.EvidenceStatus;
import com.horsetrust.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvidenceService {

        private final EvidenceRepository evidenceRepository;
        private final ListingRepository listingRepository;
        private final HorseRepository horseRepository;
        private final UserRepository userRepository;
        private final CloudinaryService cloudinaryService;

        @Transactional
        public EvidenceResponse upload(CreateEvidenceRequest req, MultipartFile file, UUID uploaderId) {
                if (req.listingId() == null && req.horseId() == null) {
                        throw new IllegalArgumentException("At least one of listingId or horseId must be provided");
                }

                User uploader = userRepository.findById(uploaderId)
                                .orElseThrow(() -> new NotFoundException("User not found"));

                Listing listing = null;
                if (req.listingId() != null) {
                        listing = listingRepository.findByIdAndSeller_Id(req.listingId(), uploaderId)
                                        .orElseThrow(() -> new ForbiddenOperationException(
                                                        "Listing not found or does not belong to you"));
                }

                Horse horse = null;
                if (req.horseId() != null) {
                        horse = horseRepository.findByIdAndOwner_Id(req.horseId(), uploaderId)
                                        .orElseThrow(() -> new ForbiddenOperationException(
                                                        "Horse not found or does not belong to you"));
                }

                String fileUrl = "";
                String publicId = "";

                if (file != null && !file.isEmpty()) {
                        try {
                                Map<String, Object> uploadResult = cloudinaryService.upload(file,
                                                "evidences/" + req.type().name());
                                fileUrl = uploadResult.get("secure_url").toString();
                                publicId = uploadResult.get("public_id").toString();
                        } catch (IOException e) {
                                throw new RuntimeException("Failed to upload evidence file to Cloudinary");
                        }
                }

                Evidence evidence = Evidence.builder()
                                .type(req.type())
                                .fileUrl(fileUrl)
                                .cloudinaryPublicId(publicId)
                                .description(req.description())
                                .metadata(req.metadata())
                                .status(EvidenceStatus.PENDING_REVIEW)
                                .listing(listing)
                                .horse(horse)
                                .uploader(uploader)
                                .build();

                Evidence saved = evidenceRepository.save(evidence);
                return toResponse(saved);
        }

        @Transactional(readOnly = true)
        public List<EvidenceResponse> getByListing(UUID listingId, UUID userId) {
                listingRepository.findByIdAndSeller_Id(listingId, userId)
                                .orElseThrow(() -> new ForbiddenOperationException(
                                                "Listing not found or does not belong to you"));

                return evidenceRepository.findAllByListing_Id(listingId).stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<EvidenceResponse> getByHorse(UUID horseId, UUID userId) {
                horseRepository.findByIdAndOwner_Id(horseId, userId)
                                .orElseThrow(() -> new ForbiddenOperationException(
                                                "Horse not found or does not belong to you"));

                return evidenceRepository.findAllByHorse_Id(horseId).stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public PageResponse<EvidenceResponse> search(com.horsetrust.models.enums.EvidenceType type,
                        EvidenceStatus status,
                        UUID listingId, UUID horseId, UUID uploaderId, Pageable pageable) {
                Specification<Evidence> spec = Specification
                                .where(com.horsetrust.repositories.specifications.EvidenceSpecification.isType(type))
                                .and(com.horsetrust.repositories.specifications.EvidenceSpecification.isStatus(status))
                                .and(com.horsetrust.repositories.specifications.EvidenceSpecification
                                                .hasListing(listingId))
                                .and(com.horsetrust.repositories.specifications.EvidenceSpecification.hasHorse(horseId))
                                .and(com.horsetrust.repositories.specifications.EvidenceSpecification
                                                .hasUploader(uploaderId));

                Page<Evidence> page = evidenceRepository.findAll(spec, pageable);
                return PageResponse.from(page.map(this::toResponse));
        }

        @Transactional
        public void delete(UUID evidenceId, UUID userId) {
                Evidence evidence = evidenceRepository.findByIdAndUploader_Id(evidenceId, userId)
                                .orElseThrow(() -> new NotFoundException("Evidence not found"));

                if (evidence.getStatus() != EvidenceStatus.PENDING_REVIEW) {
                        throw new ForbiddenOperationException(
                                        "Only evidence with status PENDING_REVIEW can be deleted");
                }

                try {
                        if (evidence.getCloudinaryPublicId() != null) {
                                cloudinaryService.delete(evidence.getCloudinaryPublicId());
                        }
                } catch (IOException e) {
                        // Log it, but delete the DB record anyway or throw exception depending on
                        // policy
                        System.err.println("Failed to delete Cloudinary asset: " + evidence.getCloudinaryPublicId());
                }

                evidenceRepository.delete(evidence);
        }

        private EvidenceResponse toResponse(Evidence e) {
                return new EvidenceResponse(
                                e.getId(),
                                e.getType().name(),
                                e.getStatus().name(),
                                e.getFileUrl(),
                                e.getDescription(),
                                e.getMetadata(),
                                e.getListing() != null ? e.getListing().getId() : null,
                                e.getHorse() != null ? e.getHorse().getId() : null,
                                e.getUploader().getId(),
                                e.getUploadedAt());
        }
}
