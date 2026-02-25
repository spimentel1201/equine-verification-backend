package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.EvidenceService;
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
@RequestMapping("/api/evidences")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenceResponse upload(
            @Valid @RequestBody CreateEvidenceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.upload(request, principal.getId());
    }

    @GetMapping("/listing/{listingId}")
    @PreAuthorize("hasRole('SELLER')")
    public List<EvidenceResponse> getByListing(
            @PathVariable UUID listingId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.getByListing(listingId, principal.getId());
    }

    @GetMapping("/horse/{horseId}")
    @PreAuthorize("hasRole('SELLER')")
    public List<EvidenceResponse> getByHorse(
            @PathVariable UUID horseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.getByHorse(horseId, principal.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        evidenceService.delete(id, principal.getId());
    }
}
