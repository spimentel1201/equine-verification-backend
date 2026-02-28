package com.horsetrust.api.controllers;

import com.horsetrust.api.dto.UpdateMeRequest;
import com.horsetrust.api.dto.UserProfileResponse;
import com.horsetrust.security.UserPrincipal;
import com.horsetrust.services.UsersService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de usuarios")
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/me")
    public UserProfileResponse me(
            @AuthenticationPrincipal UserPrincipal principal) {
        return usersService.me(principal.getId());
    }

    @PutMapping("/me")
    public UserProfileResponse updateMe(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateMeRequest req) {
        return usersService.updateMe(principal.getId(), req);
    }
}