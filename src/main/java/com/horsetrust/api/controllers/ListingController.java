package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.ListingService;
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
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ListingResponse create(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return listingService.createListing(request, principal.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ListingResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return listingService.updateListing(id, request, principal.getId());
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('SELLER')")
    public List<ListingResponse> myListings(@AuthenticationPrincipal UserPrincipal principal) {
        return listingService.myListings(principal.getId());
    }
}