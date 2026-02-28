package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Evidences", description = "Subida y consulta de evidencias")
@SecurityRequirement(name = "bearerAuth")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subir evidencia", description = "Sube una evidencia asociada a un anuncio o un caballo. Al menos uno de los dos debe indicarse.")
    public EvidenceResponse upload(
            @Valid @RequestBody CreateEvidenceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.upload(request, principal.getId());
    }

    @GetMapping("/listing/{listingId}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Evidencias por anuncio", description = "Lista todas las evidencias asociadas a un anuncio del vendedor autenticado.")
    public List<EvidenceResponse> getByListing(
            @PathVariable UUID listingId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.getByListing(listingId, principal.getId());
    }

    @GetMapping("/horse/{horseId}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Evidencias por caballo", description = "Lista todas las evidencias asociadas a un caballo del vendedor autenticado.")
    public List<EvidenceResponse> getByHorse(
            @PathVariable UUID horseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.getByHorse(horseId, principal.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar evidencia", description = "Elimina una evidencia propia, solo si su estado es PENDING_REVIEW.")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        evidenceService.delete(id, principal.getId());
    }
}
