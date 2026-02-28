package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listings")
@Tag(name = "Listings", description = "Gestión de anuncios de venta de caballos")
@SecurityRequirement(name = "bearerAuth")
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Crear listing", description = "Crea un nuevo anuncio de venta en estado DRAFT. Requiere rol SELLER.")
    public ListingResponse create(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return listingService.createListing(request, principal.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Actualizar listing", description = "Edita un listing propio en estado DRAFT o REJECTED.")
    public ListingResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return listingService.updateListing(id, request, principal.getId());
    }

    @PostMapping("/{id}/rollback")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Rollback a DRAFT (RF-12)", description = "Revierte un listing REJECTED a DRAFT para permitir correcciones y re-envío.")
    public ListingResponse rollbackToDraft(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return listingService.rollbackToDraft(id, principal.getId());
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Mis listings", description = "Devuelve todos los listings del seller autenticado.")
    public List<ListingResponse> myListings(@AuthenticationPrincipal UserPrincipal principal) {
        return listingService.myListings(principal.getId());
    }
}