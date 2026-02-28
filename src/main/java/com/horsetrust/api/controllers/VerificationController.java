package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.VerificationService;
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
@RequestMapping("/api/verifications")
@Tag(name = "Verifications", description = "Flujo de verificación de listings (RF-07/08)")
@SecurityRequirement(name = "bearerAuth")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Solicitar verificación (RF-05/06)", description = "El seller solicita verificación de un listing en DRAFT o REJECTED. El listing pasa a PENDING_VERIFICATION.")
    public VerificationResponse requestVerification(
            @Valid @RequestBody RequestVerificationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return verificationService.requestVerification(request.listingId(), principal.getId());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verificaciones pendientes (ADMIN)", description = "Lista todas las verificaciones con estado PENDING. Exclusivo para administradores.")
    public List<VerificationResponse> getPending() {
        return verificationService.getPending();
    }

    @GetMapping("/listing/{listingId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Historial de verificaciones", description = "Devuelve el historial de verificaciones de un listing específico.")
    public List<VerificationResponse> getByListing(@PathVariable UUID listingId) {
        return verificationService.getByListing(listingId);
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revisar verificación (RF-07/08)", description = "El admin aprueba (APPROVED→listing VERIFIED), rechaza (REJECTED→listing REJECTED) o pide info adicional (NEEDS_INFO).")
    public VerificationResponse review(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewVerificationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return verificationService.review(id, request, principal.getId());
    }
}
