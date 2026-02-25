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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HorseService {

    private final HorseRepository horseRepository;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;

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

    // Para cuando implementes PUT /horses/{id}
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

    private String normalize(String s) {
        return s == null ? null : s.trim().replaceAll("\\s+", " ");
    }

    private HorseResponse toResponse(Horse h) {
        return new HorseResponse(
                h.getId(),
                h.getName(),
                h.getBreed(),
                h.getAge(),
                h.getGender(),
                h.getOwner().getId(),
                h.getCreatedAt());
    }
}