package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.HorseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;

    // "Registra la identidad inmutable de un caballo (requiere rol SELLER)"
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public HorseResponse register(
            @Valid @RequestBody CreateHorseRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return horseService.register(request, principal.getId());
    }

    // "Devuelve la lista de caballos registrados por el usuario autenticado"
    @GetMapping("/my-horses")
    @PreAuthorize("hasRole('SELLER')")
    public List<HorseResponse> myHorses(@AuthenticationPrincipal UserPrincipal principal) {
        return horseService.myHorses(principal.getId());
    }
}