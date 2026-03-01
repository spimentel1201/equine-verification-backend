package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    @Operation(summary = "Crear listing", description = "Crea un nuevo anuncio de venta en estado DRAFT. Requiere rol VENDEDOR o SELLER.")
    public ListingResponse create(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return listingService.createListing(request, principal.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Actualizar anuncio", description = "Edita un anuncio propio en estado DRAFT o REJECTED.")
    public ListingResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return listingService.updateListing(id, request, principal.getId());
    }

    @PostMapping("/{id}/rollback")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Rollback a borrador(estado DRAFT)", description = "Revierte un anuncio en estado rechazado(REJECTED) a borrador(DRAFT) para permitir correcciones y re-envío.")
    public ListingResponse rollbackToDraft(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return listingService.rollbackToDraft(id, principal.getId());
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Mis anuncios", description = "Devuelve todos los anuncios del vendedor autenticado.")
    public List<ListingResponse> myListings(@AuthenticationPrincipal UserPrincipal principal) {
        return listingService.myListings(principal.getId());
    }

    @GetMapping
    @Operation(summary = "Buscar anuncios", description = "Busca todos los caballos verificados con filtros y paginación.")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements // Limpia los requirements globales para este endpoint
    public PageResponse<ListingResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String breed,
            @PageableDefault(size = 10) Pageable pageable) {
        return listingService.findActiveListings(q, minPrice, maxPrice, location, breed, pageable);
    }
}