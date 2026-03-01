package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.*;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.HorseService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/horses")
@Tag(name = "Horses", description = "Gestión de caballos")
@SecurityRequirement(name = "bearerAuth")
public class HorseController {

    private final HorseService horseService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Registrar caballo", description = "Registra la identidad inmutable(no editable) de un caballo. Requiere rol VENDEDOR o SELLER.")
    public HorseResponse register(
            @Valid @RequestBody CreateHorseRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return horseService.register(request, principal.getId());
    }

    @GetMapping("/my-horses")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Mis caballos", description = "Devuelve los caballos registrados por el vendedor autenticado.")
    public List<HorseResponse> myHorses(@AuthenticationPrincipal UserPrincipal principal) {
        return horseService.myHorses(principal.getId());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar caballos", description = "Lista y busca caballos registrados en la plataforma mediante filtros dinámicos.")
    public PageResponse<HorseResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) com.horsetrust.models.enums.HorseGender gender,
            @PageableDefault(size = 10) Pageable pageable) {
        return horseService.search(name, breed, minAge, maxAge, gender, pageable);
    }

    @PostMapping(value = "/{id}/photos", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    @Operation(summary = "Subir foto de caballo", description = "Sube una foto a la galería del caballo. Límite: 5 fotos.")
    public HorsePhotoResponse addPhoto(
            @PathVariable java.util.UUID id,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        return horseService.addPhoto(id, file, principal.getId());
    }

    @DeleteMapping("/{horseId}/photos/{photoId}")
    @PreAuthorize("hasRole('SELLER')")
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar foto de caballo", description = "Elimina una foto de la galería particular del caballo.")
    public void deletePhoto(
            @PathVariable java.util.UUID horseId,
            @PathVariable java.util.UUID photoId,
            @AuthenticationPrincipal UserPrincipal principal) {
        horseService.deletePhoto(horseId, photoId, principal.getId());
    }
}