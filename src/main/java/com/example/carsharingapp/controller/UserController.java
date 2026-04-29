package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserResponseWithRolesDto;
import com.example.carsharingapp.dto.user.UserUpdateProfileRequestDto;
import com.example.carsharingapp.dto.user.UserUpdateRolesRequestDto;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User management", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View user", description = "Get a specific user's profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserResponseDto getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfile(user);
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user", description = "Update a specific user's profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserResponseDto updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UserUpdateProfileRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfile(user.getId(), requestDto);
    }

    @PutMapping("/{userId}/role")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update roles", description = "Update a specific user's roles")
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponseWithRolesDto updateUserRoles(
            @PathVariable @Positive Long userId,
            @RequestBody UserUpdateRolesRequestDto requestDto) {
        return userService.updateUserRoles(userId, requestDto);
    }

}
