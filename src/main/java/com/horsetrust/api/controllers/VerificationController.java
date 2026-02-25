package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verifications")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    public VerificationResponse requestVerification(
            @Valid @RequestBody RequestVerificationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return verificationService.requestVerification(request.listingId(), principal.getId());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<VerificationResponse> getPending() {
        return verificationService.getPending();
    }

    @GetMapping("/listing/{listingId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public List<VerificationResponse> getByListing(@PathVariable UUID listingId) {
        return verificationService.getByListing(listingId);
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public VerificationResponse review(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewVerificationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return verificationService.review(id, request, principal.getId());
    }
}
