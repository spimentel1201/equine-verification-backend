package com.horsetrust.services;

import com.horsetrust.api.dto.UpdateMeRequest;
import com.horsetrust.api.dto.UserProfileResponse;
import com.horsetrust.common.exception.NotFoundException;
import com.horsetrust.models.entities.User;
import com.horsetrust.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse me(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toProfile(u);
    }

    @Transactional
    public UserProfileResponse updateMe(UUID userId, UpdateMeRequest req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (req.phone() != null) u.setPhone(req.phone());
        if (req.bio() != null) u.setBio(req.bio());
        if (req.profileImageUrl() != null) u.setProfileImageUrl(req.profileImageUrl());

        return toProfile(u);
    }

    private UserProfileResponse toProfile(User u) {
        return new UserProfileResponse(
                u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(),
                u.getRole(), u.getStatus(), u.getPhone(), u.getBio(),
                u.getProfileImageUrl(), u.getCreatedAt(), u.getUpdatedAt()
        );
    }
}