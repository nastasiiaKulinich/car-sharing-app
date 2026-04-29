package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalDto;
import com.example.carsharingapp.dto.rental.ReturnRentalRequestDto;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/{rentalId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a rental by id",
            description = "Admin can get detailed information about any specific rental. "
                    + "User can get detailed information about only his specific rental.")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    public RentalDto getRentalById(Authentication authentication,
                                   @PathVariable @Positive Long rentalId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        boolean isAdmin = isAdmin(authentication);
        return rentalService.findById(userId, rentalId, isAdmin);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a list of rentals",
            description = "Get rentals with optional filters. "
                    + "Customers can see only their rentals. "
                    + "Managers can see all rentals"
                    + "Use is_active to filter active rentals.")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    public List<RentalDto> getRentalsByUserId(
            Authentication authentication,
            @RequestParam(name = "user_id", required = false) @Positive Long userId,
            @RequestParam(name = "is_active", required = false) Boolean isActive,
            Pageable pageable) {
        User authUser = (User) authentication.getPrincipal();
        Long authUserId = authUser.getId();
        boolean isAdmin = isAdmin(authentication);
        return rentalService.findByUserIdAndActive(authUserId, isAdmin, userId, isActive, pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new rental", description = "Create a new rental")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    public RentalDto createRental(Authentication authentication,
                                  @RequestBody @Valid CreateRentalRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.save(user.getId(), requestDto);
    }

    @PutMapping("/{rentalId}/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update rental by id", description = "Set actual return date")
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    public RentalDto setActualReturnDate(@PathVariable @Positive Long rentalId,
                                         @RequestBody @Valid ReturnRentalRequestDto requestDto) {
        return rentalService.setActualReturnDate(rentalId, requestDto);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_MANAGER"));
    }
}
