package com.example.carsharingapp.controller;

import com.example.carsharingapp.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View user payments",
            description = "Endpoint for retrieving a list of payments of specific user")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public List<PaymentDetailedResponseDto> getUserPayments(
            Authentication authentication,
            @RequestParam("user_id") Long userId,
            Pageable pageable) {
        User authUser = (User) authentication.getPrincipal();
        Long authUserId = authUser.getId();
        boolean isAdmin = isAdmin(authentication);
        return paymentService.findAll(authUserId, isAdmin, userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create session",
            description = "Endpoint for creating a session for rental payment")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public PaymentResponseDto createPaymentSession(
            Authentication authentication,
            @RequestBody @Valid CreatePaymentRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return paymentService.createPaymentSession(userId, requestDto);
    }

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View success",
            description = "Endpoint for viewing a successful payment page")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public PaymentStatusResponseDto handleSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View cancel",
            description = "Endpoint for viewing a canceled payment page")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public PaymentStatusResponseDto handleCancel(@RequestParam("session_id") String sessionId) {
        return paymentService.handleCancel(sessionId);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_MANAGER"));
    }
}
