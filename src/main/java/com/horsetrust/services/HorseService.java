package com.horsetrust.services;

import com.horsetrust.api.dto.*;
import com.horsetrust.common.exception.*;
import com.horsetrust.models.entities.Horse;
import com.horsetrust.models.entities.User;
import com.horsetrust.repositories.HorseRepository;
import com.horsetrust.repositories.UserRepository; // asumido
import com.horsetrust.repositories.VerificationRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorseService {

        private final HorseRepository horseRepository;
        private final UserRepository userRepository;
        private final VerificationRepository verificationRepository;
        private final com.horsetrust.repositories.HorsePhotoRepository horsePhotoRepository;
        private final CloudinaryService cloudinaryService;

        @Transactional
        public HorseResponse register(CreateHorseRequest req, UUID ownerId) {
                User owner = userRepository.findById(ownerId)
                                .orElseThrow(() -> new NotFoundException("User not found"));

                Horse horse = Horse.builder()
                                .name(normalize(req.name()))
                                .breed(normalize(req.breed()))
                                .age(req.age())
                                .gender(req.gender())
                                .owner(owner)
                                .build();

                Horse saved = horseRepository.save(horse);
                return toResponse(saved);
        }

        @Transactional(readOnly = true)
        public List<HorseResponse> myHorses(UUID ownerId) {
                return horseRepository.findAllByOwner_Id(ownerId).stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public PageResponse<HorseResponse> search(String name, String breed, Integer minAge, Integer maxAge,
                        com.horsetrust.models.enums.HorseGender gender, Pageable pageable) {
                Specification<Horse> spec = Specification
                                .where(com.horsetrust.repositories.specifications.HorseSpecification.nameContains(name))
                                .and(com.horsetrust.repositories.specifications.HorseSpecification.breedContains(breed))
                                .and(com.horsetrust.repositories.specifications.HorseSpecification.ageBetween(minAge,
                                                maxAge))
                                .and(com.horsetrust.repositories.specifications.HorseSpecification.isGender(gender));

                Page<Horse> page = horseRepository.findAll(spec, pageable);
                return PageResponse.from(page.map(this::toResponse));
        }

        @Transactional(readOnly = true)
        public void assertIdentityNotLocked(UUID horseId) {
                // Verificar si algún listing de este horse tiene verificaciones históricas
                List<UUID> listingIds = horseRepository.findById(horseId)
                                .map(horse -> horse.getListings().stream()
                                                .map(listing -> listing.getId())
                                                .toList())
                                .orElse(List.of());

                boolean locked = listingIds.stream()
                                .anyMatch(id -> verificationRepository.existsByTargetAndTargetId(
                                                com.horsetrust.models.enums.VerificationTarget.LISTING, id));
                if (locked) {
                        throw new ForbiddenOperationException(
                                        "Horse identity data cannot be changed because it is linked to historical verifications");
                }
        }

        @Transactional
        public HorsePhotoResponse addPhoto(UUID horseId, MultipartFile file, UUID ownerId) {
                Horse horse = horseRepository.findByIdAndOwner_Id(horseId, ownerId)
                                .orElseThrow(() -> new ForbiddenOperationException(
                                                "Horse not found or does not belong to you"));

                long currentPhotosCount = horsePhotoRepository.countByHorse_Id(horseId);
                if (currentPhotosCount >= 5) {
                        throw new IllegalArgumentException(
                                        "Maximum of 5 photos allowed per horse");
                }

                try {
                        Map<String, Object> uploadResult = cloudinaryService.upload(file, "horses/" + horseId);
                        String fileUrl = uploadResult.get("secure_url").toString();
                        String publicId = uploadResult.get("public_id").toString();

                        com.horsetrust.models.entities.HorsePhoto photo = com.horsetrust.models.entities.HorsePhoto
                                        .builder()
                                        .imageUrl(fileUrl)
                                        .cloudinaryPublicId(publicId)
                                        .displayOrder((int) currentPhotosCount)
                                        .horse(horse)
                                        .build();

                        com.horsetrust.models.entities.HorsePhoto saved = horsePhotoRepository.save(photo);
                        return new HorsePhotoResponse(saved.getId(), saved.getImageUrl(), saved.getDisplayOrder(),
                                        saved.getUploadedAt());
                } catch (IOException e) {
                        throw new RuntimeException("Failed to upload horse photo to Cloudinary");
                }
        }

        @Transactional
        public void deletePhoto(UUID horseId, UUID photoId, UUID ownerId) {
                Horse horse = horseRepository.findByIdAndOwner_Id(horseId, ownerId)
                                .orElseThrow(() -> new ForbiddenOperationException(
                                                "Horse not found or does not belong to you"));

                com.horsetrust.models.entities.HorsePhoto photo = horsePhotoRepository.findById(photoId)
                                .orElseThrow(() -> new NotFoundException("Photo not found"));

                if (!photo.getHorse().getId().equals(horse.getId())) {
                        throw new ForbiddenOperationException("Photo does not belong to this horse");
                }

                try {
                        if (photo.getCloudinaryPublicId() != null) {
                                cloudinaryService.delete(photo.getCloudinaryPublicId());
                        }
                } catch (IOException e) {
                        System.err.println("Failed to delete Cloudinary asset: " + photo.getCloudinaryPublicId());
                }

                horsePhotoRepository.delete(photo);
        }

        private String normalize(String s) {
                return s == null ? null : s.trim().replaceAll("\\s+", " ");
        }

        private HorseResponse toResponse(Horse h) {
                List<HorsePhotoResponse> photos = h.getPhotos() == null ? List.of()
                                : h.getPhotos().stream().map(p -> new HorsePhotoResponse(
                                                p.getId(), p.getImageUrl(), p.getDisplayOrder(), p.getUploadedAt()))
                                                .collect(Collectors.toList());

                return new HorseResponse(
                                h.getId(),
                                h.getName(),
                                h.getBreed(),
                                h.getAge(),
                                h.getGender(),
                                h.getOwner().getId(),
                                h.getCreatedAt(),
                                photos);
        }
}