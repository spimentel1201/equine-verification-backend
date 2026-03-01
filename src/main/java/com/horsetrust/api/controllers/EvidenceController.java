package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.horsetrust.models.enums.EvidenceStatus;
import com.horsetrust.models.enums.EvidenceType;
import com.horsetrust.models.enums.UserRole;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evidences")
@Tag(name = "Evidences", description = "Subida y consulta de evidencias")
@SecurityRequirement(name = "bearerAuth")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subir evidencia", description = "Sube una evidencia (archivo) asociada a un anuncio o un caballo. Al menos uno de los dos debe indicarse.")
    public EvidenceResponse upload(
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file,
            @Valid @RequestPart("data") CreateEvidenceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return evidenceService.upload(request, file, principal.getId());
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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar Evidencias", description = "Los vendedores solo ven sus evidencias. Los ADMIN ven todas. Soporta filtros dinámicos.")
    public PageResponse<EvidenceResponse> search(
            @RequestParam(required = false) EvidenceType type,
            @RequestParam(required = false) EvidenceStatus status,
            @RequestParam(required = false) UUID listingId,
            @RequestParam(required = false) UUID horseId,
            @RequestParam(required = false) UUID uploaderId,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

        // Regla de negocio: Si no es Admin, forzar a que SÓLO pueda ver sus Propias
        // Evidencias
        boolean isAdmin = principal.getRole() == UserRole.ADMIN;

        UUID finalUploaderId = isAdmin ? uploaderId : principal.getId();

        return evidenceService.search(type, status, listingId, horseId, finalUploaderId, pageable);
    }
}
